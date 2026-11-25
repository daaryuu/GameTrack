package gametrack.front.cliente;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Envia as requisições ao servidor e converte as respostas de erro em
 * {@link ErroServidorException}. É usada por todos os clientes HTTP.
 */
final class ConexaoHttp {

    private static final String CABECALHO_TIPO = "Content-Type";
    private static final String TIPO_JSON = "application/json; charset=UTF-8";
    private static final int STATUS_SUCESSO_MAXIMO = 299;
    private static final Duration TEMPO_LIMITE = Duration.ofSeconds(10);
    private static final String MENSAGEM_SEM_CONEXAO = "Não foi possível conectar ao servidor!";
    private static final String MENSAGEM_ERRO = "Erro inesperado no servidor!";

    private final String urlBase;
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    ConexaoHttp(String urlBase) {
        this.urlBase = urlBase;
    }

    <T> T get(String caminho, Class<T> tipo) {
        return gson.fromJson(enviar(novaRequisicao(caminho).GET()), tipo);
    }

    void post(String caminho, Object corpo) {
        enviar(novaRequisicao(caminho).header(CABECALHO_TIPO, TIPO_JSON).POST(json(corpo)));
    }

    void put(String caminho, Object corpo) {
        enviar(novaRequisicao(caminho).header(CABECALHO_TIPO, TIPO_JSON).PUT(json(corpo)));
    }

    void delete(String caminho) {
        enviar(novaRequisicao(caminho).DELETE());
    }

    private HttpRequest.Builder novaRequisicao(String caminho) {
        return HttpRequest.newBuilder(URI.create(urlBase + caminho)).timeout(TEMPO_LIMITE);
    }

    private HttpRequest.BodyPublisher json(Object corpo) {
        return HttpRequest.BodyPublishers.ofString(gson.toJson(corpo));
    }

    private String enviar(HttpRequest.Builder requisicao) {
        try {
            HttpResponse<String> resposta =
                    http.send(requisicao.build(), HttpResponse.BodyHandlers.ofString());
            verificarStatus(resposta);
            return resposta.body();
        } catch (IOException e) {
            throw new ErroServidorException(MENSAGEM_SEM_CONEXAO, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ErroServidorException(MENSAGEM_SEM_CONEXAO, e);
        }
    }

    private void verificarStatus(HttpResponse<String> resposta) {
        if (resposta.statusCode() > STATUS_SUCESSO_MAXIMO) {
            throw new ErroServidorException(extrairMensagem(resposta.body()));
        }
    }

    private String extrairMensagem(String corpo) {
        try {
            RespostaErro erro = gson.fromJson(corpo, RespostaErro.class);
            return erro != null && erro.mensagem() != null ? erro.mensagem() : MENSAGEM_ERRO;
        } catch (JsonSyntaxException e) {
            return MENSAGEM_ERRO;
        }
    }

    private record RespostaErro(String mensagem) {
    }
}
