package gametrack.front.cliente;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;

import com.google.gson.Gson;

import gametrack.front.dto.FiltroJogoDTO;
import gametrack.front.dto.JogoDTO;

public class JogoClienteHttp implements JogoCliente{
    private static final String CAMINHO_JOGOS = "/jogos";
    private static final String CABECALHO_TIPO = "Content-Type";
    private static final String TIPO_JSON = "application/json";
    private static final int STATUS_SUCESSO_MAXIMO = 299;
    private static final Duration TEMPO_LIMITE = Duration.ofSeconds(10);
    private static final String MENSAGEM_SEM_CONEXAO = "Não foi possível conectar ao servidor!";

    private final String urlBase;
    private final HttpClient http;
    private final Gson gson;

    public JogoClienteHttp(String urlBase){
        this.urlBase = urlBase;
        this.http = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

     @Override
    public List<JogoDTO> listar(FiltroJogoDTO filtro) {
        HttpRequest requisicao = novaRequisicao(CAMINHO_JOGOS + montarConsulta(filtro))
                .GET()
                .build();
        return List.of(gson.fromJson(enviar(requisicao), JogoDTO[].class));
    }

    @Override
    public JogoDTO buscarPorId(int id) {
        HttpRequest requisicao = novaRequisicao(caminhoDoJogo(id)).GET().build();
        return gson.fromJson(enviar(requisicao), JogoDTO.class);
    }

    @Override
    public void cadastrar(JogoDTO jogo) {
        HttpRequest requisicao = novaRequisicao(CAMINHO_JOGOS)
                .header(CABECALHO_TIPO, TIPO_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(jogo)))
                .build();
        enviar(requisicao);
    }

    @Override
    public void alterar(JogoDTO jogo) {
        HttpRequest requisicao = novaRequisicao(caminhoDoJogo(jogo.id()))
                .header(CABECALHO_TIPO, TIPO_JSON)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(jogo)))
                .build();
        enviar(requisicao);
    }

    @Override
    public void excluir(int id) {
        HttpRequest requisicao = novaRequisicao(caminhoDoJogo(id)).DELETE().build();
        enviar(requisicao);
    }

    private HttpRequest.Builder novaRequisicao(String caminho) {
        return HttpRequest.newBuilder(URI.create(urlBase + caminho)).timeout(TEMPO_LIMITE);
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

    private String enviar(HttpRequest requisicao) {
        try {
            HttpResponse<String> resposta =
                    http.send(requisicao, HttpResponse.BodyHandlers.ofString());
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
            RespostaErro erro = gson.fromJson(resposta.body(), RespostaErro.class);
            throw new ErroServidorException(erro.mensagem());
        }
    }

    private record RespostaErro(String mensagem) {
    }
}
