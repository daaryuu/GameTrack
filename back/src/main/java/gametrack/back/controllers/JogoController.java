package gametrack.back.controllers;

import gametrack.back.config.Mensagens;
import gametrack.back.model.DadosJogoDTO;
import gametrack.back.model.FiltroJogoDTO;
import gametrack.back.model.JogoDTO;
import gametrack.back.services.JogoService;
import java.util.List;

/**
 * Recebe solicitações relacionadas ao catálogo de jogos e delega ao serviço.
 *
 * @see RF003
 * @see RF004
 * @see RF004.1
 */
public class JogoController {

    private final JogoService jogoService;

    /**
     * Constrói o controlador injetando o serviço de jogos.
     *
     * @param jogoService serviço com as regras de negócio do catálogo
     */
    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    /**
     * Consulta os jogos do catálogo aplicando os filtros informados.
     *
     * @param usuarioAutenticadoId identificador do usuário autenticado
     * @param filtro critérios de filtro enviados pelo cliente
     * @return lista de jogos encontrados
     */
    public List<JogoDTO> listarJogos(Integer usuarioAutenticadoId, FiltroJogoDTO filtro) {
        return jogoService.listarCatalogo(usuarioAutenticadoId, filtro);
    }

    /**
     * Processa a solicitação de cadastro de um jogo.
     *
     * @param usuarioAutenticadoId identificador do usuário autenticado
     * @param dados dados do jogo enviados pelo cliente
     * @return mensagem de sucesso
     */
    public String cadastrarJogo(Integer usuarioAutenticadoId, DadosJogoDTO dados) {
        jogoService.cadastrar(usuarioAutenticadoId, dados);
        return Mensagens.JOGO_CADASTRADO_SUCESSO;
    }
}