package gametrack.back.routes;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import gametrack.back.config.Mensagens;
import gametrack.back.services.NegocioException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Lê o corpo JSON das requisições.
 */
final class RequisicaoJson {

    private static final Gson GSON = new Gson();

    private RequisicaoJson() {
    }

    static <T> T ler(HttpExchange troca, Class<T> tipo) throws IOException {
        String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            return GSON.fromJson(corpo, tipo);
        } catch (JsonParseException e) {
            throw new NegocioException(Mensagens.CORPO_INVALIDO);
        }
    }
}
