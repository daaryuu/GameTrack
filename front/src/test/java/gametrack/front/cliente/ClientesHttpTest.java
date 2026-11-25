package gametrack.front.cliente;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gametrack.front.dto.CadastroUsuarioDTO;
import gametrack.front.dto.CredenciaisDTO;
import gametrack.front.dto.FiltroJogoDTO;
import gametrack.front.dto.JogoDTO;
import gametrack.front.dto.Perfil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes dos clientes HTTP com um servidor falso em porta livre, que guarda a requisição
 * recebida e devolve a resposta preparada.
 *
 * @see RF001
 * @see RF002
 * @see RF004
 */
class ClientesHttpTest {

    private static final CadastroUsuarioDTO CADASTRO =
            new CadastroUsuarioDTO("joão", "senha1234");
    private static final CredenciaisDTO CREDENCIAIS =
            new CredenciaisDTO("joão", "senha1234");
    private static final JogoDTO PORTAL =
            new JogoDTO(5, "Portal 2", "Quebra-cabeça", "Valve", 2011);

    private HttpServer servidor;
    private UsuarioCliente usuarios;
    private SessaoCliente sessoes;
    private JogoCliente jogos;
    private volatile String credencialRecebida;
    private int status = 200;
    private String resposta = "";
    private volatile String requisicaoRecebida;
    private volatile String corpoRecebido;
    private volatile String tipoRecebido;

    @BeforeEach
    void iniciarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", this::responder);
        servidor.start();
        String url = "http://localhost:" + servidor.getAddress().getPort();
        ConexaoHttp conexao = new ConexaoHttp(url);
        usuarios = new UsuarioClienteHttp(conexao);
        sessoes = new SessaoClienteHttp(conexao);
        jogos = new JogoClienteHttp(conexao);
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    @Test
    void deveEnviarOCadastroDeConta() {
        preparar(201, "{\"mensagem\":\"Conta cadastrada com sucesso!\"}");

        assertDoesNotThrow(() -> usuarios.cadastrar(CADASTRO));

        assertEquals("POST /usuarios", requisicaoRecebida);
        assertEquals("application/json; charset=UTF-8", tipoRecebido);
        assertEquals(CADASTRO, new Gson().fromJson(corpoRecebido, CadastroUsuarioDTO.class));
    }

    @Test
    void deveEnviarAsCredenciaisEUsarOTokenNasProximasRequisicoes() {
        preparar(201, "{\"token\":\"abc123\"}");

        sessoes.autenticar(CREDENCIAIS);

        assertEquals("POST /sessoes", requisicaoRecebida);
        assertEquals(CREDENCIAIS, new Gson().fromJson(corpoRecebido, CredenciaisDTO.class));
        assertNull(credencialRecebida);
        preparar(200, "[]");
        jogos.listar(FiltroJogoDTO.semFiltro());
        assertEquals("Bearer abc123", credencialRecebida);
    }

    @Test
    void deveDevolverOPerfilDaContaOuODeMenorAcesso() {
        preparar(201, "{\"token\":\"abc\",\"perfil\":\"ADMINISTRADOR\"}");
        assertEquals(Perfil.ADMINISTRADOR, sessoes.autenticar(CREDENCIAIS));

        preparar(201, "{\"token\":\"abc\",\"perfil\":\"DESCONHECIDO\"}");
        assertEquals(Perfil.USUARIO, sessoes.autenticar(CREDENCIAIS));
    }

    @Test
    void naoDeveEnviarTokenQuandoOLoginForRecusado() {
        preparar(401, "{\"mensagem\":\"Apelido ou senha incorretos!\"}");

        ErroServidorException erro = assertThrows(
                ErroServidorException.class, () -> sessoes.autenticar(CREDENCIAIS));

        assertEquals("Apelido ou senha incorretos!", erro.getMessage());
        preparar(200, "[]");
        jogos.listar(FiltroJogoDTO.semFiltro());
        assertNull(credencialRecebida);
    }

    @Test
    void deveDistinguirAFaltaDeAutenticacaoDosOutrosErros() {
        preparar(401, "{\"mensagem\":\"Usuário não autenticado.\"}");

        NaoAutenticadoException erro = assertThrows(NaoAutenticadoException.class,
                () -> jogos.listar(FiltroJogoDTO.semFiltro()));

        assertEquals("Usuário não autenticado.", erro.getMessage());
    }

    @Test
    void deveRecusarLoginSemToken() {
        for (String corpo : List.of("", "{}", "{\"token\":\"\"}")) {
            preparar(201, corpo);

            assertThrows(ErroServidorException.class, () -> sessoes.autenticar(CREDENCIAIS));
        }
    }

    @Test
    void deveRepassarAMensagemDeErroDoServidor() {
        preparar(400, "{\"mensagem\":\"Apelido já utilizado!\"}");

        conferirErro("Apelido já utilizado!");
    }

    @Test
    void deveUsarMensagemPadraoQuandoOErroNaoTemJson() {
        preparar(500, "<html>erro</html>");
        conferirErro("Erro inesperado no servidor!");

        preparar(500, "");
        conferirErro("Erro inesperado no servidor!");
    }

    @Test
    void deveInformarQuandoNaoHaConexao() {
        servidor.stop(0);

        conferirErro("Não foi possível conectar ao servidor!");
    }

    @Test
    void deveListarJogosComFiltro() {
        preparar(200, new Gson().toJson(List.of(PORTAL)));

        assertEquals(List.of(PORTAL),
                jogos.listar(new FiltroJogoDTO("portal 2", null, " ", "2011")));
        assertEquals("GET /jogos?titulo=portal+2&ano=2011", requisicaoRecebida);
    }

    @Test
    void deveEnviarCadaOperacaoDoCatalogo() {
        preparar(200, new Gson().toJson(PORTAL));

        assertEquals(PORTAL, jogos.buscarPorId(5));
        assertEquals("GET /jogos/5", requisicaoRecebida);
        jogos.cadastrar(PORTAL);
        assertEquals("POST /jogos", requisicaoRecebida);
        jogos.alterar(PORTAL);
        assertEquals("PUT /jogos/5", requisicaoRecebida);
        assertEquals(PORTAL, new Gson().fromJson(corpoRecebido, JogoDTO.class));
        jogos.excluir(5);
        assertEquals("DELETE /jogos/5", requisicaoRecebida);
    }

    private void preparar(int novoStatus, String novaResposta) {
        status = novoStatus;
        resposta = novaResposta;
    }

    private void conferirErro(String mensagem) {
        ErroServidorException erro = assertThrows(
                ErroServidorException.class, () -> usuarios.cadastrar(CADASTRO));
        assertEquals(mensagem, erro.getMessage());
    }

    private void responder(HttpExchange troca) throws IOException {
        requisicaoRecebida = troca.getRequestMethod() + " " + troca.getRequestURI();
        credencialRecebida = troca.getRequestHeaders().getFirst("Authorization");
        tipoRecebido = troca.getRequestHeaders().getFirst("Content-Type");
        corpoRecebido = new String(
                troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
        troca.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        troca.getResponseBody().write(bytes);
        troca.close();
    }
}
