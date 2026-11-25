package gametrack.back.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.SessaoController;
import gametrack.back.model.SessaoDTO;
import gametrack.back.model.Usuario;
import gametrack.back.repositories.PersistenciaException;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.seguranca.SessoesEmMemoria;
import gametrack.back.services.AutenticacaoService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes da rota de login com um servidor HTTP real, em porta livre, e repository falso.
 *
 * @see RF002
 * @see RNF003
 */
class SessaoRoutesTest {

    private static final int PORTA_LIVRE = 0;
    private static final String LOGIN_VALIDO =
            "{\"apelido\":\" joão \",\"senha\":\"senha1234\"}";

    private final SessoesEmMemoria sessoes =
            new SessoesEmMemoria(Duration.ofMinutes(30), Clock.systemUTC());
    private final UsuarioRepositoryFalso repositorio = new UsuarioRepositoryFalso();
    private HttpServer servidor;

    @BeforeEach
    void iniciarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", PORTA_LIVRE), 0);
        AutenticacaoService service = new AutenticacaoService(
                repositorio, (senha, hash) -> hash.equals("hash_" + senha), sessoes);
        new SessaoRoutes(new SessaoController(service)).registrar(servidor);
        servidor.start();
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    @Test
    void deveResponder201ComTokenDaSessaoDoUsuario() throws Exception {
        HttpResponse<String> resposta = post(LOGIN_VALIDO);

        assertEquals(201, resposta.statusCode());
        assertFalse(resposta.body().contains("hash_"));
        String token = new Gson().fromJson(resposta.body(), SessaoDTO.class).token();
        assertEquals(7, sessoes.exigirUsuarioAutenticado("Bearer " + token));
    }

    @Test
    void deveResponder401ParaCredenciaisIncorretas() throws Exception {
        for (String corpo : List.of(
                "{\"apelido\":\"joão\",\"senha\":\"errada99\"}",
                "{\"apelido\":\"maria\",\"senha\":\"senha1234\"}",
                "{\"apelido\":\"joão\"}",
                "{\"senha\":\"senha1234\"}",
                "{\"apelido\":\" \",\"senha\":\"\"}",
                "")) {
            conferir(post(corpo), 401, Mensagens.CREDENCIAIS_INCORRETAS);
        }
    }

    @Test
    void deveResponder400ParaJsonInvalido() throws Exception {
        conferir(post("{\"apelido\":"), 400, Mensagens.CORPO_INVALIDO);
    }

    @Test
    void deveResponder405ParaMetodoDiferenteDePost() throws Exception {
        conferir(enviar(HttpRequest.newBuilder(uri()).GET().build()),
                405, Mensagens.METODO_NAO_PERMITIDO);
    }

    @Test
    void deveResponder500QuandoOBancoFalhar() throws Exception {
        repositorio.falha = new PersistenciaException(new RuntimeException("sem conexão"));

        conferir(post(LOGIN_VALIDO), 500, Mensagens.ERRO_COMUNICACAO_BANCO);
    }

    private void conferir(HttpResponse<String> resposta, int status, String mensagem) {
        assertEquals(status, resposta.statusCode());
        assertTrue(resposta.body().contains(mensagem), resposta.body());
    }

    private URI uri() {
        return URI.create("http://localhost:" + servidor.getAddress().getPort() + "/sessoes");
    }

    private HttpResponse<String> post(String corpo) throws Exception {
        return enviar(HttpRequest.newBuilder(uri())
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build());
    }

    private HttpResponse<String> enviar(HttpRequest requisicao) throws Exception {
        return HttpClient.newHttpClient().send(requisicao, HttpResponse.BodyHandlers.ofString());
    }

    private static final class UsuarioRepositoryFalso implements UsuarioRepository {

        private final Usuario joao = new Usuario(7, "joão", "hash_senha1234");
        private RuntimeException falha;

        @Override
        public boolean existePorApelido(String apelido) {
            return buscarPorApelido(apelido).isPresent();
        }

        @Override
        public Optional<Usuario> buscarPorApelido(String apelido) {
            if (falha != null) {
                throw falha;
            }
            return Optional.of(joao).filter(conta -> conta.getApelido().equals(apelido));
        }

        @Override
        public Usuario inserir(Usuario usuario) {
            throw new UnsupportedOperationException();
        }
    }
}
