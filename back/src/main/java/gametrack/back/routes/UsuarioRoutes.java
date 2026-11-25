package gametrack.back.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.UsuarioController;
import gametrack.back.model.CadastroUsuarioDTO;
import java.io.IOException;

/**
 * Registra e trata as rotas HTTP de conta. O cadastro não exige autenticação.
 * Respostas: 201 quando a conta é criada, 400 para JSON inválido ou dados recusados, 405 para
 * métodos diferentes de POST e 500 para falhas no banco ou inesperadas. Todas vêm como
 * {@code {"mensagem": "..."}}.
 *
 * @see RF001
 * @see RNF003
 */
public class UsuarioRoutes {

    private static final String CAMINHO_USUARIOS = "/usuarios";
    private static final String METODO_POST = "POST";

    private final UsuarioController usuarioController;

    /**
     * Constrói o gerenciador de rotas de conta.
     *
     * @param usuarioController controlador de conta
     */
    public UsuarioRoutes(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    /**
     * Vincula o endpoint {@code /usuarios} ao servidor HTTP.
     *
     * @param servidor instância do servidor HTTP
     */
    public void registrar(HttpServer servidor) {
        servidor.createContext(CAMINHO_USUARIOS, this::tratarRequisicao);
    }

    private void tratarRequisicao(HttpExchange troca) throws IOException {
        if (!METODO_POST.equalsIgnoreCase(troca.getRequestMethod())) {
            RespostaHttp.enviarMensagem(troca, RespostaHttp.STATUS_METODO_NAO_PERMITIDO,
                    Mensagens.METODO_NAO_PERMITIDO);
            return;
        }
        try {
            String mensagem = usuarioController.cadastrarConta(
                    RequisicaoJson.ler(troca, CadastroUsuarioDTO.class));
            RespostaHttp.enviarMensagem(troca, RespostaHttp.STATUS_CRIADO, mensagem);
        } catch (RuntimeException e) {
            RespostaHttp.enviarFalha(troca, e);
        }
    }
}
