package gametrack.front.cliente;

import gametrack.front.dto.FiltroJogoDTO;
import gametrack.front.dto.JogoDTO;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;

/**
 * Implementação de {@link JogoCliente} que fala com o servidor por HTTP.
 *
 * @see RF004
 */
public class JogoClienteHttp implements JogoCliente {

    private static final String CAMINHO_JOGOS = "/jogos";

    private final ConexaoHttp conexao;

    /**
     * Cria o cliente.
     *
     * @param urlBase endereço do servidor, como {@code http://localhost:8080}
     */
    public JogoClienteHttp(String urlBase) {
        this.conexao = new ConexaoHttp(urlBase);
    }

    @Override
    public List<JogoDTO> listar(FiltroJogoDTO filtro) {
        return List.of(conexao.get(CAMINHO_JOGOS + montarConsulta(filtro), JogoDTO[].class));
    }

    @Override
    public JogoDTO buscarPorId(int id) {
        return conexao.get(caminhoDoJogo(id), JogoDTO.class);
    }

    @Override
    public void cadastrar(JogoDTO jogo) {
        conexao.post(CAMINHO_JOGOS, jogo);
    }

    @Override
    public void alterar(JogoDTO jogo) {
        conexao.put(caminhoDoJogo(jogo.id()), jogo);
    }

    @Override
    public void excluir(int id) {
        conexao.delete(caminhoDoJogo(id));
    }

    private String caminhoDoJogo(int id) {
        return CAMINHO_JOGOS + "/" + id;
    }

    private String montarConsulta(FiltroJogoDTO filtro) {
        StringJoiner parametros = new StringJoiner("&", "?", "");
        parametros.setEmptyValue("");
        adicionarParametro(parametros, "titulo", filtro.titulo());
        adicionarParametro(parametros, "genero", filtro.genero());
        adicionarParametro(parametros, "desenvolvedora", filtro.desenvolvedora());
        adicionarParametro(parametros, "ano", filtro.ano());
        return parametros.toString();
    }

    private void adicionarParametro(StringJoiner parametros, String nome, String valor) {
        if (valor != null && !valor.isBlank()) {
            parametros.add(nome + "=" + URLEncoder.encode(valor.trim(), StandardCharsets.UTF_8));
        }
    }
}
