package gametrack.back.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import gametrack.back.config.Mensagens;
import gametrack.back.controllers.SessaoController;
import gametrack.back.model.CredenciaisDTO;
import gametrack.back.model.SessaoDTO;
import java.io.IOException;

/**
 * Registra e trata a rota HTTP de login.
 * Respostas: 201 com {@code {"token": "..."}}, 400 para JSON inválido, 401 para credenciais
 * incorretas, 405 para métodos diferentes de POST e 500 para falhas no banco ou inesperadas.
 * Os erros vêm como {@code {"mensagem": "..."}}.
 *
 * @see RF002
 * @see RNF003
 */
public class SessaoRoutes {

    private static final String CAMINHO_SESSOES = "/sessoes";
    private static final String METODO_POST = "POST";

    private final SessaoController sessaoController;

    /**
     * Constrói o gerenciador da rota de login.
     *
     * @param sessaoController controlador de sessão
     */
    public SessaoRoutes(SessaoController sessaoController) {
        this.sessaoController = sessaoController;
    }

    /**
     * Vincula o endpoint {@code /sessoes} ao servidor HTTP.
     *
     * @param servidor instância do servidor HTTP
     */
    public void registrar(HttpServer servidor) {
        servidor.createContext(CAMINHO_SESSOES, this::tratarRequisicao);
    }

    private void tratarRequisicao(HttpExchange troca) throws IOException {
        if (!METODO_POST.equalsIgnoreCase(troca.getRequestMethod())) {
            RespostaHttp.enviarMensagem(troca, RespostaHttp.STATUS_METODO_NAO_PERMITIDO,
                    Mensagens.METODO_NAO_PERMITIDO);
            return;
        }
        try {
            SessaoDTO sessao = sessaoController.autenticar(
                    RequisicaoJson.ler(troca, CredenciaisDTO.class));
            RespostaHttp.enviarJson(troca, RespostaHttp.STATUS_CRIADO, sessao);
        } catch (RuntimeException e) {
            RespostaHttp.enviarFalha(troca, e);
        }
    }
}
