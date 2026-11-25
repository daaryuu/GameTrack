package gametrack.back.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.UsuarioController;
import gametrack.back.model.Usuario;
import gametrack.back.repositories.PersistenciaException;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.services.UsuarioService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes da rota de cadastro de conta com um servidor HTTP real, em porta livre, e repository
 * falso.
 *
 * @see RF001
 * @see RNF003
 */
class UsuarioRoutesTest {

    private static final int PORTA_LIVRE = 0;
    private static final String CADASTRO_VALIDO =
            "{\"apelido\":\"joão\",\"senha\":\"senha1234\"}";

    private final UsuarioRepositoryFalso repositorio = new UsuarioRepositoryFalso();
    private HttpServer servidor;

    @BeforeEach
    void iniciarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", PORTA_LIVRE), 0);
        UsuarioService service = new UsuarioService(repositorio, senha -> "hash_" + senha);
        new UsuarioRoutes(new UsuarioController(service)).registrar(servidor);
        servidor.start();
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    @Test
    void deveResponder201EGravarAContaComHash() throws Exception {
        HttpResponse<String> resposta = post(CADASTRO_VALIDO);

        conferir(resposta, 201, Mensagens.CONTA_CADASTRADA_SUCESSO);
        assertEquals("joão", repositorio.inserido.getApelido());
        assertEquals("hash_senha1234", repositorio.inserido.getSenhaHash());
    }

    @Test
    void deveResponder400QuandoASenhaNaoForInformada() throws Exception {
        conferir(post("{\"apelido\":\"pedro\"}"), 400, Mensagens.SENHA_NAO_INFORMADA);
        assertNull(repositorio.inserido);
    }

    @Test
    void deveResponder400QuandoOApelidoJaExistir() throws Exception {
        repositorio.apelidoEmUso = true;

        conferir(post(CADASTRO_VALIDO), 400, Mensagens.APELIDO_JA_UTILIZADO);
    }

    @Test
    void deveResponder400ParaCorpoVazio() throws Exception {
        conferir(post(""), 400, Mensagens.APELIDO_NAO_INFORMADO);
    }

    @Test
    void deveResponder400ParaJsonInvalido() throws Exception {
        conferir(post("{\"apelido\":"), 400, Mensagens.CORPO_INVALIDO);
        conferir(post("[]"), 400, Mensagens.CORPO_INVALIDO);
    }

    @Test
    void deveResponder405ParaMetodoDiferenteDePost() throws Exception {
        conferir(enviar(HttpRequest.newBuilder(uri()).GET().build()),
                405, Mensagens.METODO_NAO_PERMITIDO);
    }

    @Test
    void deveResponder500QuandoOBancoFalhar() throws Exception {
        repositorio.falha = new PersistenciaException(new RuntimeException("sem conexão"));

        conferir(post(CADASTRO_VALIDO), 500, Mensagens.ERRO_COMUNICACAO_BANCO);
    }

    @Test
    void deveResponder500ParaErroInesperado() throws Exception {
        repositorio.falha = new IllegalStateException("falha qualquer");

        conferir(post(CADASTRO_VALIDO), 500, Mensagens.ERRO_INESPERADO);
    }

    private void conferir(HttpResponse<String> resposta, int status, String mensagem) {
        assertEquals(status, resposta.statusCode());
        assertTrue(resposta.body().contains(mensagem), resposta.body());
    }

    private URI uri() {
        return URI.create("http://localhost:" + servidor.getAddress().getPort() + "/usuarios");
    }

    private HttpResponse<String> post(String corpo) throws Exception {
        return enviar(HttpRequest.newBuilder(uri())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build());
    }

    private HttpResponse<String> enviar(HttpRequest requisicao) throws Exception {
        return HttpClient.newHttpClient().send(requisicao, HttpResponse.BodyHandlers.ofString());
    }

    private static final class UsuarioRepositoryFalso implements UsuarioRepository {

        private boolean apelidoEmUso;
        private RuntimeException falha;
        private Usuario inserido;

        @Override
        public boolean existePorApelido(String apelido) {
            if (falha != null) {
                throw falha;
            }
            return apelidoEmUso;
        }

        @Override
        public Optional<Usuario> buscarPorApelido(String apelido) {
            return Optional.empty();
        }

        @Override
        public Usuario inserir(Usuario usuario) {
            usuario.setId(1);
            inserido = usuario;
            return usuario;
        }
    }
}
