package gametrack.back.controllers;

import gametrack.back.config.Mensagens;
import gametrack.back.model.CadastroUsuarioDTO;
import gametrack.back.model.Usuario;
import gametrack.back.repositories.PersistenciaException;
import gametrack.back.services.NegocioException;
import gametrack.back.services.UsuarioService;

/**
 * Recebe requisições relacionadas à conta do usuário e delega ao serviço.
 *
 * @see RF001
 */
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Constrói o controlador injetando o serviço de usuários.
     *
     * @param usuarioService serviço com as regras de negócio de conta
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Processa a solicitação de cadastro de uma nova conta.
     *
     * @param dados DTO contendo apelido e senha
     * @return mensagem de sucesso ou erro correspondente ao fluxo executado
     */
    public String cadastrarConta(CadastroUsuarioDTO dados) {
        try {
            Usuario criado = usuarioService.cadastrar(dados);
            return Mensagens.CONTA_CADASTRADA_SUCESSO + " ID: " + criado.getId();
        } catch (NegocioException | PersistenciaException e) {
            return e.getMessage();
        }
    }
}