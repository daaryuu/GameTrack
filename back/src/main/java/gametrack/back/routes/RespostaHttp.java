package gametrack.back.routes;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import gametrack.back.config.Mensagens;
import gametrack.back.repositories.PersistenciaException;
import gametrack.back.seguranca.AutenticacaoException;
import gametrack.back.seguranca.PermissaoException;
import gametrack.back.services.NegocioException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Envia as respostas JSON das rotas e converte as exceções do sistema em status HTTP.
 *
 * @see RNF003
 */
final class RespostaHttp {

    static final int STATUS_OK = 200;
    static final int STATUS_CRIADO = 201;
    static final int STATUS_REQUISICAO_INVALIDA = 400;
    static final int STATUS_NAO_AUTENTICADO = 401;
    static final int STATUS_SEM_PERMISSAO = 403;
    static final int STATUS_METODO_NAO_PERMITIDO = 405;
    static final int STATUS_ERRO_INTERNO = 500;

    private static final Logger LOGGER = Logger.getLogger(RespostaHttp.class.getName());
    private static final String CABECALHO_TIPO = "Content-Type";
    private static final String TIPO_JSON = "application/json; charset=UTF-8";
    private static final Gson GSON = new Gson();

    private RespostaHttp() {
    }

    static void enviarJson(HttpExchange troca, int status, Object corpo) throws IOException {
        byte[] bytes = GSON.toJson(corpo).getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().set(CABECALHO_TIPO, TIPO_JSON);
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    static void enviarMensagem(HttpExchange troca, int status, String mensagem)
            throws IOException {
        enviarJson(troca, status, new RespostaMensagem(mensagem));
    }

    static void enviarFalha(HttpExchange troca, RuntimeException falha) throws IOException {
        switch (falha) {
            case AutenticacaoException e ->
                    enviarMensagem(troca, STATUS_NAO_AUTENTICADO, e.getMessage());
            case PermissaoException e ->
                    enviarMensagem(troca, STATUS_SEM_PERMISSAO, e.getMessage());
            case NegocioException e ->
                    enviarMensagem(troca, STATUS_REQUISICAO_INVALIDA, e.getMessage());
            case PersistenciaException e -> {
                LOGGER.log(Level.SEVERE, "Falha ao acessar o banco.", e);
                enviarMensagem(troca, STATUS_ERRO_INTERNO, e.getMessage());
            }
            default -> {
                LOGGER.log(Level.SEVERE, "Erro inesperado ao tratar a requisição.", falha);
                enviarMensagem(troca, STATUS_ERRO_INTERNO, Mensagens.ERRO_INESPERADO);
            }
        }
    }

    private record RespostaMensagem(String mensagem) {
    }
}
