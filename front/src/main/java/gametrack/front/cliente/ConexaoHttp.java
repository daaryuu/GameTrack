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
 * {@link ErroServidorException}. Todos os clientes HTTP devem compartilhar a mesma conexão,
 * pois é ela que guarda o token da sessão e o envia em cada requisição.
 *
 * @see RF002
 */
public final class ConexaoHttp {

    private static final String CABECALHO_AUTH = "Authorization";
    private static final String PREFIXO_CREDENCIAL = "Bearer ";
    private static final String CABECALHO_TIPO = "Content-Type";
    private static final String TIPO_JSON = "application/json; charset=UTF-8";
    private static final int STATUS_SUCESSO_MAXIMO = 299;
    private static final int STATUS_NAO_AUTENTICADO = 401;
    private static final Duration TEMPO_LIMITE = Duration.ofSeconds(10);
    private static final String MENSAGEM_SEM_CONEXAO = "Não foi possível conectar ao servidor!";
    private static final String MENSAGEM_ERRO = "Erro inesperado no servidor!";

    private final String urlBase;
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // As requisições rodam em threads do SwingWorker, diferentes a cada envio.
    private volatile String token;

    /**
     * Cria a conexão, ainda sem sessão.
     *
     * @param urlBase endereço do servidor, como {@code http://localhost:8080}
     */
    public ConexaoHttp(String urlBase) {
        this.urlBase = urlBase;
    }

    void definirToken(String novoToken) {
        this.token = novoToken;
    }

    <T> T get(String caminho, Class<T> tipo) {
        return gson.fromJson(enviar(novaRequisicao(caminho).GET()), tipo);
    }

    <T> T post(String caminho, Object corpo, Class<T> tipo) {
        return gson.fromJson(enviarPost(caminho, corpo), tipo);
    }

    void post(String caminho, Object corpo) {
        enviarPost(caminho, corpo);
    }

    private String enviarPost(String caminho, Object corpo) {
        return enviar(novaRequisicao(caminho)
                .header(CABECALHO_TIPO, TIPO_JSON).POST(json(corpo)));
    }

    void put(String caminho, Object corpo) {
        enviar(novaRequisicao(caminho).header(CABECALHO_TIPO, TIPO_JSON).PUT(json(corpo)));
    }

    void delete(String caminho) {
        enviar(novaRequisicao(caminho).DELETE());
    }

    private HttpRequest.Builder novaRequisicao(String caminho) {
        HttpRequest.Builder requisicao =
                HttpRequest.newBuilder(URI.create(urlBase + caminho)).timeout(TEMPO_LIMITE);
        String tokenAtual = token;
        if (tokenAtual != null) {
            requisicao.header(CABECALHO_AUTH, PREFIXO_CREDENCIAL + tokenAtual);
        }
        return requisicao;
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
        if (resposta.statusCode() == STATUS_NAO_AUTENTICADO) {
            throw new NaoAutenticadoException(extrairMensagem(resposta.body()));
        }
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
