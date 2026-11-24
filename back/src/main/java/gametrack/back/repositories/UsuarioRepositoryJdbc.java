package gametrack.back.repositories;

import gametrack.back.config.Mensagens;
import gametrack.back.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementação JDBC do repositório de usuários para MySQL.
 *
 * @see RF001
 * @see RF001.1
 * @see RNF003
 */
public class UsuarioRepositoryJdbc implements UsuarioRepository {

    private static final String SQL_EXISTE_APELIDO =
            "SELECT 1 FROM Usuario WHERE apelido = ? LIMIT 1";
    private static final String SQL_INSERIR_USUARIO =
            "INSERT INTO Usuario (apelido, senha_hash) VALUES (?, ?)";

    private final Connection conexao;

    /**
     * Constrói o repositório com uma conexão ativa com o banco.
     *
     * @param conexao conexão JDBC configurada
     */
    public UsuarioRepositoryJdbc(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Consulta no banco se o apelido já está associado a outra conta.
     *
     * @param apelido apelido a consultar
     * @return verdadeiro caso exista registro correspondente
     */
    @Override
    public boolean existePorApelido(String apelido) {
        try (PreparedStatement stmt = conexao.prepareStatement(SQL_EXISTE_APELIDO)) {
            stmt.setQueryTimeout(Mensagens.TIMEOUT_BANCO_SEGUNDOS);
            stmt.setString(1, apelido);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException(e);
        }
    }

    /**
     * Grava o novo usuário no banco e atribui o ID gerado pela chave primária.
     *
     * @param usuario usuário validado com hash bcrypt
     * @return usuário com ID preenchido
     */
    @Override
    public Usuario inserir(Usuario usuario) {
        try (PreparedStatement stmt = conexao.prepareStatement(
                SQL_INSERIR_USUARIO, Statement.RETURN_GENERATED_KEYS)) {
            configurarParametrosInsercao(stmt, usuario);
            stmt.executeUpdate();
            usuario.setId(extrairIdGerado(stmt));
            return usuario;
        } catch (SQLException e) {
            throw new PersistenciaException(e);
        }
    }

    private void configurarParametrosInsercao(PreparedStatement stmt, Usuario usuario)
            throws SQLException {
        stmt.setQueryTimeout(Mensagens.TIMEOUT_BANCO_SEGUNDOS);
        stmt.setString(1, usuario.getApelido());
        stmt.setString(2, usuario.getSenhaHash());
    }

    private int extrairIdGerado(PreparedStatement stmt) throws SQLException {
        try (ResultSet chaves = stmt.getGeneratedKeys()) {
            if (chaves.next()) {
                return chaves.getInt(1);
            }
            throw new SQLException("Falha ao obter ID gerado para o usuário.");
        }
    }
}