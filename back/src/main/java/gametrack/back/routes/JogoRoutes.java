package gametrack.back.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.JogoController;
import gametrack.back.model.DadosJogoDTO;
import gametrack.back.model.FiltroJogoDTO;
import gametrack.back.model.JogoDTO;
import gametrack.back.seguranca.VerificadorAutenticacao;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registra e trata as rotas HTTP do catálogo de jogos.
 * O GET responde 200 com a lista (vazia quando nada atende ao filtro), e o POST, exclusivo do
 * administrador, responde 201 quando o jogo é cadastrado. Os erros são 400 para dados
 * inválidos, 401 sem autenticação, 403 sem permissão, 405 para outros métodos e 500 para
 * falhas no banco ou inesperadas, sempre como {@code {"mensagem": "..."}}.
 *
 * @see RF003
 * @see RF004
 * @see RF004.1
 * @see RNF003
 */
public class JogoRoutes {

    private static final String CAMINHO_JOGOS = "/jogos";
    private static final String METODO_GET = "GET";
    private static final String METODO_POST = "POST";
    private static final String CABECALHO_AUTH = "Authorization";

    private final JogoController jogoController;
    private final VerificadorAutenticacao verificadorAutenticacao;

    /**
     * Constrói o gerenciador de rotas de jogos.
     *
     * @param jogoController controlador do catálogo
     * @param verificadorAutenticacao validador de usuário autenticado
     */
    public JogoRoutes(
            JogoController jogoController, VerificadorAutenticacao verificadorAutenticacao) {
        this.jogoController = jogoController;
        this.verificadorAutenticacao = verificadorAutenticacao;
    }

    /**
     * Vincula o endpoint {@code /jogos} ao servidor HTTP.
     *
     * @param servidor instância do servidor HTTP
     */
    public void registrar(HttpServer servidor) {
        servidor.createContext(CAMINHO_JOGOS, this::tratarRequisicao);
    }

    private void tratarRequisicao(HttpExchange troca) throws IOException {
        try {
            switch (troca.getRequestMethod()) {
                case METODO_GET -> listarJogos(troca);
                case METODO_POST -> cadastrarJogo(troca);
                default -> RespostaHttp.enviarMensagem(troca,
                        RespostaHttp.STATUS_METODO_NAO_PERMITIDO, Mensagens.METODO_NAO_PERMITIDO);
            }
        } catch (IllegalArgumentException e) {
            RespostaHttp.enviarMensagem(troca, RespostaHttp.STATUS_REQUISICAO_INVALIDA,
                    Mensagens.PARAMETROS_INVALIDOS);
        } catch (RuntimeException e) {
            RespostaHttp.enviarFalha(troca, e);
        }
    }

    private void listarJogos(HttpExchange troca) throws IOException {
        List<JogoDTO> jogos = jogoController.listarJogos(
                exigirUsuario(troca), extrairFiltro(troca.getRequestURI()));
        RespostaHttp.enviarJson(troca, RespostaHttp.STATUS_OK, jogos);
    }

    private void cadastrarJogo(HttpExchange troca) throws IOException {
        // A autenticação vem antes da leitura, para não ler dados de quem não entrou.
        Integer usuarioId = exigirUsuario(troca);
        String mensagem = jogoController.cadastrarJogo(
                usuarioId, RequisicaoJson.ler(troca, DadosJogoDTO.class));
        RespostaHttp.enviarMensagem(troca, RespostaHttp.STATUS_CRIADO, mensagem);
    }

    private Integer exigirUsuario(HttpExchange troca) {
        String credencial = troca.getRequestHeaders().getFirst(CABECALHO_AUTH);
        return verificadorAutenticacao.exigirUsuarioAutenticado(credencial);
    }

    private FiltroJogoDTO extrairFiltro(URI uri) {
        Map<String, String> parametros = lerParametros(uri.getRawQuery());
        return new FiltroJogoDTO(
                parametros.get("titulo"),
                parametros.get("genero"),
                parametros.get("desenvolvedora"),
                parametros.get("ano"));
    }

    private Map<String, String> lerParametros(String consultaBruta) {
        Map<String, String> mapa = new HashMap<>();
        if (consultaBruta == null || consultaBruta.isBlank()) {
            return mapa;
        }
        for (String par : consultaBruta.split("&")) {
            String[] partes = par.split("=", 2);
            String valor = partes.length > 1 ? partes[1] : "";
            mapa.put(decodificar(partes[0]), decodificar(valor));
        }
        return mapa;
    }

    private String decodificar(String texto) {
        return URLDecoder.decode(texto, StandardCharsets.UTF_8);
    }
}
