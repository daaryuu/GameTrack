package gametrack.back.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.JogoController;
import gametrack.back.model.Jogo;
import gametrack.back.model.Perfil;
import gametrack.back.repositories.JogoRepositoryFalso;
import gametrack.back.repositories.PersistenciaException;
import gametrack.back.seguranca.AutenticacaoException;
import gametrack.back.seguranca.SessoesEmMemoria;
import gametrack.back.seguranca.VerificadorAutenticacao;
import gametrack.back.services.JogoService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Testes das rotas do catálogo com um servidor HTTP real, em porta livre, e repository falso.
 * Conferem o contrato combinado com o front: códigos de status e formato das respostas.
 *
 * @see RF003
 * @see RF004
 * @see RF005
 * @see RNF003
 */
class JogoRoutesTest {

    private static final int PORTA_LIVRE = 0;
    private static final Jogo PORTAL = new Jogo(5, "Portal 2", "Quebra-cabeça", "Valve", 2011);

    private static final int ADMINISTRADOR_ID = 1;
    private static final String JOGO_VALIDO = "{\"titulo\":\"T\",\"genero\":\"Ação\","
            + "\"desenvolvedora\":\"D\",\"ano\":2018}";

    private HttpServer servidor;
    private JogoRepositoryFalso repositorio;

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
    void deveExigirOTokenDeUmaSessaoAberta() throws Exception {
        SessoesEmMemoria sessoes =
                new SessoesEmMemoria(Duration.ofMinutes(30), Clock.systemUTC());
        iniciar((t, g, d, a) -> List.of(PORTAL), sessoes);

        assertEquals(401, get("/jogos").statusCode());
        assertEquals(401, getComCredencial("Bearer inventado").statusCode());
        assertEquals(200, getComCredencial("Bearer " + sessoes.abrir(1)).statusCode());
    }

    @Test
    void deveResponder201QuandoOAdministradorCadastraUmJogo() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> ADMINISTRADOR_ID);

        HttpResponse<String> resposta = post(JOGO_VALIDO);

        assertEquals(201, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.JOGO_CADASTRADO_SUCESSO));
        assertEquals("Ação", repositorio.inseridos().get(0).getGenero());
    }

    @Test
    void deveResponder403QuandoUmUsuarioComumCadastraUmJogo() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> 2);

        HttpResponse<String> resposta = post(JOGO_VALIDO);

        assertEquals(403, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.EXCLUSIVO_ADMINISTRADOR));
        assertTrue(repositorio.inseridos().isEmpty());
    }

    @Test
    void deveResponder401NoCadastroSemAutenticacao() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> {
            throw new AutenticacaoException();
        });

        assertEquals(401, post(JOGO_VALIDO).statusCode());
        assertTrue(repositorio.inseridos().isEmpty());
    }

    @Test
    void deveResponder400ParaDadosInvalidosNoCadastro() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> ADMINISTRADOR_ID);

        assertTrue(post("").body().contains(Mensagens.JOGO_DADOS_NAO_INFORMADOS));
        assertTrue(post("{\"titulo\":").body().contains(Mensagens.CORPO_INVALIDO));
        HttpResponse<String> anoEmTexto = post(JOGO_VALIDO.replace("2018", "\"20a\""));
        assertEquals(400, anoEmTexto.statusCode());
        assertTrue(anoEmTexto.body().contains(Mensagens.CORPO_INVALIDO));
    }

    @Test
    void deveBuscarUmJogoPorId() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> 2);
        repositorio.definirGravado(PORTAL);

        HttpResponse<String> resposta = get("/jogos/5");

        assertEquals(200, resposta.statusCode());
        assertTrue(resposta.body().startsWith("{\"id\":5,\"titulo\":\"Portal 2\""));
    }

    @Test
    void deveResponder404ParaJogoInexistente() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> ADMINISTRADOR_ID);

        for (String caminho : List.of(
                "/jogos/99", "/jogos/abc", "/jogos/", "/jogos/5/x", "/jogos/99999999999")) {
            assertEquals(404, get(caminho).statusCode(), caminho);
            assertEquals(404, put(caminho, JOGO_VALIDO).statusCode(), caminho);
        }
        assertTrue(get("/jogos/99").body().contains(Mensagens.JOGO_NAO_ENCONTRADO));
    }

    @Test
    void deveAlterarUmJogoIgnorandoOIdDoCorpo() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> ADMINISTRADOR_ID);
        repositorio.definirGravado(PORTAL);

        HttpResponse<String> resposta =
                put("/jogos/5", JOGO_VALIDO.replace("{", "{\"id\":77,"));

        assertEquals(200, resposta.statusCode());
        assertTrue(resposta.body().contains(Mensagens.JOGO_ALTERADO_SUCESSO));
        assertEquals("T", repositorio.buscarPorId(5).orElseThrow().getTitulo());
    }

    @Test
    void deveRecusarAlteracaoSemPermissaoOuComDadosInvalidos() throws Exception {
        iniciar((t, g, d, a) -> List.of(),
                credencial -> credencial == null ? ADMINISTRADOR_ID : 2);
        repositorio.definirGravado(PORTAL);

        assertEquals(400, put("/jogos/5", "").statusCode());
        assertEquals(400, put("/jogos/5", "{\"titulo\":").statusCode());
        HttpResponse<String> semPermissao = enviar(HttpRequest.newBuilder(uri("/jogos/5"))
                .header("Authorization", "Bearer comum")
                .PUT(HttpRequest.BodyPublishers.ofString(JOGO_VALIDO)).build());
        assertEquals(403, semPermissao.statusCode());
        assertEquals("Portal 2", repositorio.buscarPorId(5).orElseThrow().getTitulo());
    }

    @Test
    void deveExigirAutenticacaoNasRotasDeUmJogo() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> {
            throw new AutenticacaoException();
        });

        assertEquals(401, get("/jogos/5").statusCode());
        assertEquals(401, put("/jogos/5", JOGO_VALIDO).statusCode());
    }

    @Test
    void deveResponder405ParaOutrosMetodosEmUmJogo() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> ADMINISTRADOR_ID);

        assertEquals(405, enviar(HttpRequest.newBuilder(uri("/jogos/5"))
                .POST(HttpRequest.BodyPublishers.noBody()).build()).statusCode());
    }

    @Test
    void deveResponder405ParaOutrosMetodos() throws Exception {
        iniciar((t, g, d, a) -> List.of(), credencial -> 1);
        HttpRequest requisicao = HttpRequest.newBuilder(uri("/jogos"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
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

    private void iniciar(JogoRepositoryFalso.Listagem listagem, VerificadorAutenticacao verificador)
            throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", PORTA_LIVRE), 0);
        repositorio = new JogoRepositoryFalso(listagem);
        JogoService service = new JogoService(repositorio, id -> Optional.of(
                id == ADMINISTRADOR_ID ? Perfil.ADMINISTRADOR : Perfil.USUARIO));
        JogoController controller = new JogoController(service);
        new JogoRoutes(controller, verificador).registrar(servidor);
        servidor.start();
    }

    private URI uri(String caminho) {
        return URI.create("http://localhost:" + servidor.getAddress().getPort() + caminho);
    }

    private HttpResponse<String> get(String caminho) throws Exception {
        return enviar(HttpRequest.newBuilder(uri(caminho)).GET().build());
    }

    private HttpResponse<String> post(String corpo) throws Exception {
        return enviar(HttpRequest.newBuilder(uri("/jogos"))
                .POST(HttpRequest.BodyPublishers.ofString(corpo)).build());
    }

    private HttpResponse<String> put(String caminho, String corpo) throws Exception {
        return enviar(HttpRequest.newBuilder(uri(caminho))
                .PUT(HttpRequest.BodyPublishers.ofString(corpo)).build());
    }

    private HttpResponse<String> getComCredencial(String credencial) throws Exception {
        return enviar(HttpRequest.newBuilder(uri("/jogos"))
                .header("Authorization", credencial).GET().build());
    }

    private HttpResponse<String> enviar(HttpRequest requisicao) throws Exception {
        return HttpClient.newHttpClient().send(requisicao, HttpResponse.BodyHandlers.ofString());
    }
}
