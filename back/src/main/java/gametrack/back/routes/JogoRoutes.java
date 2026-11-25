package gametrack.back.routes;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
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

/**
 * Registra e trata as rotas HTTP do catálogo de jogos.
 *
 * @see RF004
 * @see RF004.1
 * @see RNF003
 */
public class JogoRoutes {

    private static final String CAMINHO_JOGOS = "/jogos";
    private static final String CABECALHO_AUTH = "Authorization";

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
        if (!"GET".equalsIgnoreCase(troca.getRequestMethod())) {
            enviarResposta(troca, 405, gson.toJson(new RespostaErro("Método não permitido.")));
            return;
        }

        try {
            String credencial = troca.getRequestHeaders().getFirst(CABECALHO_AUTH);
            Integer usuarioId = verificadorAutenticacao.exigirUsuarioAutenticado(credencial);
            FiltroJogoDTO filtro = extrairFiltro(troca.getRequestURI());

            List<JogoDTO> jogos = jogoController.listarJogos(usuarioId, filtro);
            enviarResposta(troca, 200, gson.toJson(jogos));
        } catch (AutenticacaoException e) {
            enviarResposta(troca, 401, gson.toJson(new RespostaErro(e.getMessage())));
        } catch (NegocioException e) {
            enviarResposta(troca, 404, gson.toJson(new RespostaErro(e.getMessage())));
        } catch (PersistenciaException e) {
            enviarResposta(troca, 500, gson.toJson(new RespostaErro(e.getMessage())));
        }
    }

    private FiltroJogoDTO extrairFiltro(URI uri) {
        Map<String, String> params = lerQueryParams(uri.getRawQuery());
        return new FiltroJogoDTO(
                params.get("titulo"),
                params.get("genero"),
                params.get("desenvolvedora"),
                params.get("ano"));
    }

    private Map<String, String> lerQueryParams(String queryBruta) {
        Map<String, String> mapa = new HashMap<>();
        if (queryBruta == null || queryBruta.isBlank()) {
            return mapa;
        }
        for (String par : queryBruta.split("&")) {
            String[] partes = par.split("=", 2);
            String chave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = partes.length > 1
                    ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8)
                    : "";
            mapa.put(chave, valor);
        }
        return mapa;
    }

    private void enviarResposta(HttpExchange troca, int status, String corpoJson)
            throws IOException {
        byte[] bytes = corpoJson.getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    private record RespostaErro(String mensagem) {
    }
}