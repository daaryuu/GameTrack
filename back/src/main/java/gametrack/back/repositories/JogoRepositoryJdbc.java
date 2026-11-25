package gametrack.back.repositories;

import gametrack.back.config.Mensagens;
import gametrack.back.model.Jogo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação JDBC do repositório de jogos do catálogo para MySQL.
 *
 * @see RF004
 * @see RF004.1
 * @see RNF003
 * @see RNF004
 */
public class JogoRepositoryJdbc implements JogoRepository {

    private static final String SQL_BASE_LISTAGEM =
            "SELECT id, titulo, genero, desenvolvedora, ano FROM Jogo WHERE 1 = 1";
    private static final String CLAUSULA_ORDEM_LIMITE = " ORDER BY titulo ASC LIMIT ?";

    private static final String SQL_INSERIR =
            "INSERT INTO Jogo (titulo, genero, desenvolvedora, ano) VALUES (?, ?, ?, ?)";

    private final Connection conexao;

    /**
     * Constrói o repositório com uma conexão ativa com o banco.
     *
     * @param conexao conexão JDBC configurada
     */
    public JogoRepositoryJdbc(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Executa a consulta filtrada no catálogo respeitando o timeout e o limite máximo de itens.
     *
     * @param titulo trecho do título (opcional)
     * @param genero trecho do gênero (opcional)
     * @param desenvolvedora trecho da desenvolvedora (opcional)
     * @param ano ano exato de lançamento (opcional)
     * @return lista de jogos encontrados
     */
    @Override
    public List<Jogo> listarComFiltros(
            String titulo, String genero, String desenvolvedora, Integer ano) {
        List<Object> parametros = new ArrayList<>();
        String sql = montarConsultaSql(titulo, genero, desenvolvedora, ano, parametros);

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setQueryTimeout(Mensagens.TIMEOUT_BANCO_SEGUNDOS);
            preencherParametros(stmt, parametros);
            return lerResultados(stmt);
        } catch (SQLException e) {
            throw new PersistenciaException(e);
        }
    }

    /**
     * Grava o jogo no catálogo.
     *
     * @param jogo jogo já validado
     */
    @Override
    public void inserir(Jogo jogo) {
        try (PreparedStatement stmt = conexao.prepareStatement(SQL_INSERIR)) {
            stmt.setQueryTimeout(Mensagens.TIMEOUT_BANCO_SEGUNDOS);
            preencherParametros(stmt, List.of(jogo.getTitulo(), jogo.getGenero(),
                    jogo.getDesenvolvedora(), jogo.getAno()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(e);
        }
    }

    private String montarConsultaSql(
            String titulo,
            String genero,
            String desenvolvedora,
            Integer ano,
            List<Object> parametros) {
        StringBuilder sql = new StringBuilder(SQL_BASE_LISTAGEM);
        adicionarFiltroTexto(sql, parametros, "titulo", titulo);
        adicionarFiltroTexto(sql, parametros, "genero", genero);
        adicionarFiltroTexto(sql, parametros, "desenvolvedora", desenvolvedora);
        if (ano != null) {
            sql.append(" AND ano = ?");
            parametros.add(ano);
        }
        sql.append(CLAUSULA_ORDEM_LIMITE);
        parametros.add(Mensagens.LIMITE_MAXIMO_CATALOGO);
        return sql.toString();
    }

    private void adicionarFiltroTexto(
            StringBuilder sql, List<Object> parametros, String coluna, String valor) {
        if (valor != null && !valor.isBlank()) {
            sql.append(" AND LOWER(").append(coluna).append(") LIKE LOWER(?)");
            parametros.add("%" + valor.trim() + "%");
        }
    }

    private void preencherParametros(PreparedStatement stmt, List<Object> parametros)
            throws SQLException {
        for (int i = 0; i < parametros.size(); i++) {
            stmt.setObject(i + 1, parametros.get(i));
        }
    }

    private List<Jogo> lerResultados(PreparedStatement stmt) throws SQLException {
        List<Jogo> jogos = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                jogos.add(mapearJogo(rs));
            }
        }
        return jogos;
    }

    private Jogo mapearJogo(ResultSet rs) throws SQLException {
        return new Jogo(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("genero"),
                rs.getString("desenvolvedora"),
                rs.getInt("ano"));
    }
}