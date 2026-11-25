package gametrack.back.routes;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.UsuarioController;
import gametrack.back.model.CadastroUsuarioDTO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    private final Gson gson;

    /**
     * Constrói o gerenciador de rotas de conta.
     *
     * @param usuarioController controlador de conta
     */
    public UsuarioRoutes(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
        this.gson = new Gson();
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
            String mensagem = usuarioController.cadastrarConta(lerDados(troca));
            RespostaHttp.enviarMensagem(troca, RespostaHttp.STATUS_CRIADO, mensagem);
        } catch (JsonParseException e) {
            RespostaHttp.enviarMensagem(troca, RespostaHttp.STATUS_REQUISICAO_INVALIDA,
                    Mensagens.CORPO_INVALIDO);
        } catch (RuntimeException e) {
            RespostaHttp.enviarFalha(troca, e);
        }
    }

    private CadastroUsuarioDTO lerDados(HttpExchange troca) throws IOException {
        String corpo = new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(corpo, CadastroUsuarioDTO.class);
    }
}
