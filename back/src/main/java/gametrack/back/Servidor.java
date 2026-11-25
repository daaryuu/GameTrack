package gametrack.back;

import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Configuracao;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.JogoController;
import gametrack.back.controllers.SessaoController;
import gametrack.back.controllers.UsuarioController;
import gametrack.back.repositories.JogoRepository;
import gametrack.back.repositories.JogoRepositoryJdbc;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.repositories.UsuarioRepositoryJdbc;
import gametrack.back.routes.JogoRoutes;
import gametrack.back.routes.SessaoRoutes;
import gametrack.back.routes.UsuarioRoutes;
import gametrack.back.seguranca.BcryptCriptografiaSenha;
import gametrack.back.seguranca.SessoesEmMemoria;
import gametrack.back.seguranca.VerificadorAutenticacao;
import gametrack.back.services.AutenticacaoService;
import gametrack.back.services.JogoService;
import gametrack.back.services.UsuarioService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Clock;
import java.util.logging.Logger;

/**
 * Ponto de entrada do servidor do GameTrack. Lê o {@code config.properties}, conecta ao
 * MySQL, monta as camadas (repository, service, controller e rotas) e começa a escutar.
 *
 * @see RF001
 * @see RF002
 * @see RF004
 */
public final class Servidor {

    private static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());
    private static final int FILA_PADRAO = 0;

    private Servidor() {
    }

    /**
     * Inicia o servidor.
     *
     * @param args não utilizado
     * @throws IOException se a porta não puder ser aberta
     * @throws SQLException se não for possível conectar ao banco
     */
    public static void main(String[] args) throws IOException, SQLException {
        Configuracao configuracao = Configuracao.carregar();
        Connection conexao = abrirConexao(configuracao);
        HttpServer servidor = HttpServer.create(
                new InetSocketAddress(configuracao.portaServidor()), FILA_PADRAO);
        SessoesEmMemoria sessoes = new SessoesEmMemoria(
                configuracao.expiracaoDaSessao(), Clock.systemUTC());
        UsuarioService usuarioService = new UsuarioService(
                new UsuarioRepositoryJdbc(conexao), new BcryptCriptografiaSenha());
        usuarioService.garantirAdministrador(configuracao::senhaAdministrador);
        montarRotasDeJogo(conexao, sessoes).registrar(servidor);
        new UsuarioRoutes(new UsuarioController(usuarioService)).registrar(servidor);
        montarRotasDeSessao(conexao, sessoes).registrar(servidor);
        // Executor padrão: uma requisição por vez, o que protege a conexão JDBC compartilhada.
        servidor.setExecutor(null);
        servidor.start();
        LOGGER.info("Servidor do GameTrack escutando na porta " + configuracao.portaServidor());
    }

    private static Connection abrirConexao(Configuracao configuracao) throws SQLException {
        DriverManager.setLoginTimeout(Mensagens.TIMEOUT_BANCO_SEGUNDOS);
        return DriverManager.getConnection(
                configuracao.urlBanco(), configuracao.usuarioBanco(), configuracao.senhaBanco());
    }

    private static JogoRoutes montarRotasDeJogo(
            Connection conexao, VerificadorAutenticacao verificador) {
        JogoRepository repositorio = new JogoRepositoryJdbc(conexao);
        JogoController controller = new JogoController(new JogoService(repositorio));
        return new JogoRoutes(controller, verificador);
    }

    private static SessaoRoutes montarRotasDeSessao(
            Connection conexao, SessoesEmMemoria sessoes) {
        UsuarioRepository repositorio = new UsuarioRepositoryJdbc(conexao);
        AutenticacaoService service = new AutenticacaoService(
                repositorio, new BcryptCriptografiaSenha(), sessoes);
        return new SessaoRoutes(new SessaoController(service));
    }
}
