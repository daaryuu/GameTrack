package gametrack.back;

import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Configuracao;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.JogoController;
import gametrack.back.controllers.UsuarioController;
import gametrack.back.repositories.JogoRepository;
import gametrack.back.repositories.JogoRepositoryJdbc;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.repositories.UsuarioRepositoryJdbc;
import gametrack.back.routes.JogoRoutes;
import gametrack.back.routes.UsuarioRoutes;
import gametrack.back.seguranca.BcryptCriptografiaSenha;
import gametrack.back.seguranca.VerificadorAutenticacaoProvisorio;
import gametrack.back.services.JogoService;
import gametrack.back.services.UsuarioService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Ponto de entrada do servidor do GameTrack. Lê o {@code config.properties}, conecta ao
 * MySQL, monta as camadas (repository, service, controller e rotas) e começa a escutar.
 *
 * @see RF001
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
        montarRotasDeJogo(conexao).registrar(servidor);
        montarRotasDeUsuario(conexao).registrar(servidor);
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

    private static JogoRoutes montarRotasDeJogo(Connection conexao) {
        JogoRepository repositorio = new JogoRepositoryJdbc(conexao);
        JogoController controller = new JogoController(new JogoService(repositorio));
        return new JogoRoutes(controller, new VerificadorAutenticacaoProvisorio());
    }

    private static UsuarioRoutes montarRotasDeUsuario(Connection conexao) {
        UsuarioRepository repositorio = new UsuarioRepositoryJdbc(conexao);
        UsuarioService service =
                new UsuarioService(repositorio, new BcryptCriptografiaSenha());
        return new UsuarioRoutes(new UsuarioController(service));
    }
}
