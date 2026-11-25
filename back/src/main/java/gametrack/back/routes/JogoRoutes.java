package gametrack.back.routes;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.JogoController;
import gametrack.back.model.FiltroJogoDTO;
import gametrack.back.model.JogoDTO;
import gametrack.back.repositories.PersistenciaException;
import gametrack.back.seguranca.AutenticacaoException;
import gametrack.back.seguranca.VerificadorAutenticacao;
import gametrack.back.services.NegocioException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registra e trata as rotas HTTP do catálogo de jogos.
 * Respostas: 200 com a lista (vazia quando nada atende ao filtro), 400 para parâmetros ou
 * filtro inválidos, 401 sem autenticação, 405 para métodos diferentes de GET e 500 para
 * falhas no banco ou inesperadas. Os erros vêm como {@code {"mensagem": "..."}}.
 *
 * @see RF004
 * @see RF004.1
 * @see RNF003
 */
public class JogoRoutes {

    private static final Logger LOGGER = Logger.getLogger(JogoRoutes.class.getName());
    private static final String CAMINHO_JOGOS = "/jogos";
    private static final String METODO_GET = "GET";
    private static final String CABECALHO_AUTH = "Authorization";
    private static final String CABECALHO_TIPO = "Content-Type";
    private static final String TIPO_JSON = "application/json; charset=UTF-8";
    private static final int STATUS_OK = 200;
    private static final int STATUS_REQUISICAO_INVALIDA = 400;
    private static final int STATUS_NAO_AUTENTICADO = 401;
    private static final int STATUS_METODO_NAO_PERMITIDO = 405;
    private static final int STATUS_ERRO_INTERNO = 500;

    private final JogoController jogoController;
    private final VerificadorAutenticacao verificadorAutenticacao;
    private final Gson gson;

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
        this.gson = new Gson();
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
        if (!METODO_GET.equalsIgnoreCase(troca.getRequestMethod())) {
            enviarErro(troca, STATUS_METODO_NAO_PERMITIDO, Mensagens.METODO_NAO_PERMITIDO);
            return;
        }
        try {
            enviarResposta(troca, STATUS_OK, gson.toJson(listarJogos(troca)));
        } catch (AutenticacaoException e) {
            enviarErro(troca, STATUS_NAO_AUTENTICADO, e.getMessage());
        } catch (NegocioException e) {
            enviarErro(troca, STATUS_REQUISICAO_INVALIDA, e.getMessage());
        } catch (IllegalArgumentException e) {
            enviarErro(troca, STATUS_REQUISICAO_INVALIDA, Mensagens.PARAMETROS_INVALIDOS);
        } catch (PersistenciaException e) {
            LOGGER.log(Level.SEVERE, "Falha ao consultar o catálogo.", e);
            enviarErro(troca, STATUS_ERRO_INTERNO, e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erro inesperado ao listar o catálogo.", e);
            enviarErro(troca, STATUS_ERRO_INTERNO, Mensagens.ERRO_INESPERADO);
        }
    }

    private List<JogoDTO> listarJogos(HttpExchange troca) {
        String credencial = troca.getRequestHeaders().getFirst(CABECALHO_AUTH);
        Integer usuarioId = verificadorAutenticacao.exigirUsuarioAutenticado(credencial);
        return jogoController.listarJogos(usuarioId, extrairFiltro(troca.getRequestURI()));
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

    private void enviarErro(HttpExchange troca, int status, String mensagem) throws IOException {
        enviarResposta(troca, status, gson.toJson(new RespostaErro(mensagem)));
    }

    private void enviarResposta(HttpExchange troca, int status, String corpoJson)
            throws IOException {
        byte[] bytes = corpoJson.getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set(CABECALHO_TIPO, TIPO_JSON);
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    private record RespostaErro(String mensagem) {
    }
}
