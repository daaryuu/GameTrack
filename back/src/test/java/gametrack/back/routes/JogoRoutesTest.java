package gametrack.back.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.JogoController;
import gametrack.back.model.Jogo;
import gametrack.back.repositories.JogoRepository;
import gametrack.back.repositories.PersistenciaException;
import gametrack.back.seguranca.AutenticacaoException;
import gametrack.back.seguranca.VerificadorAutenticacao;
import gametrack.back.services.JogoService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Testes das rotas do catálogo com um servidor HTTP real, em porta livre, e repository falso.
 * Conferem o contrato combinado com o front: códigos de status e formato das respostas.
 *
 * @see RF004
 * @see RNF003
 */
class JogoRoutesTest {

    private static final int PORTA_LIVRE = 0;
    private static final Jogo PORTAL = new Jogo(5, "Portal 2", "Quebra-cabeça", "Valve", 2011);

    private HttpServer servidor;

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    @Test
    void deveResponder200ComListaDeJogos() throws Exception {
        iniciar((t, g, d, a) -> List.of(PORTAL), credencial -> 1);

        HttpResponse<String> resposta = get("/jogos");

        assertEquals(200, resposta.statusCode());
        assertTrue(resposta.body().contains("\"titulo\":\"Portal 2\""));
    }

    @Test
    void deveResponder200ComListaVaziaQuandoNadaAtendeAoFiltro() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> 1);

        HttpResponse<String> resposta = get("/jogos?titulo=zzz");

        assertEquals(200, resposta.statusCode());
        assertEquals("[]", resposta.body());
    }

    @Test
    void deveResponder400ParaAnoNaoNumerico() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> 1);

        HttpResponse<String> resposta = get("/jogos?ano=abc");

        assertEquals(400, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.ANO_FILTRO_INVALIDO));
    }

    @Test
    void deveResponder401SemAutenticacao() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> {
            throw new AutenticacaoException();
        });

        HttpResponse<String> resposta = get("/jogos");

        assertEquals(401, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.USUARIO_NAO_AUTENTICADO));
    }

    @Test
    void deveResponder405ParaMetodoDiferenteDeGet() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> 1);
        HttpRequest requisicao = HttpRequest.newBuilder(uri("/jogos"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> resposta = enviar(requisicao);

        assertEquals(405, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.METODO_NAO_PERMITIDO));
    }

    @Test
    void deveResponder500ComMensagemDoBancoQuandoAConsultaFalha() throws Exception {
        iniciar((t, g, d, a) -> {
            throw new PersistenciaException(new RuntimeException("sem conexão"));
        }, credencial -> 1);

        HttpResponse<String> resposta = get("/jogos");

        assertEquals(500, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.ERRO_COMUNICACAO_BANCO));
    }

    @Test
    void deveResponder500ParaErroInesperado() throws Exception {
        iniciar((t, g, d, a) -> {
            throw new IllegalStateException("falha qualquer");
        }, credencial -> 1);

        HttpResponse<String> resposta = get("/jogos");

        assertEquals(500, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.ERRO_INESPERADO));
    }

    private void iniciar(JogoRepository repositorio, VerificadorAutenticacao verificador)
            throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", PORTA_LIVRE), 0);
        JogoController controller = new JogoController(new JogoService(repositorio));
        new JogoRoutes(controller, verificador).registrar(servidor);
        servidor.start();
    }

    private URI uri(String caminho) {
        return URI.create("http://localhost:" + servidor.getAddress().getPort() + caminho);
    }

    private HttpResponse<String> get(String caminho) throws Exception {
        return enviar(HttpRequest.newBuilder(uri(caminho)).GET().build());
    }

    private HttpResponse<String> enviar(HttpRequest requisicao) throws Exception {
        return HttpClient.newHttpClient().send(requisicao, HttpResponse.BodyHandlers.ofString());
    }
}
