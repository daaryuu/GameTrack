package gametrack.back.controllers;

import gametrack.back.model.CredenciaisDTO;
import gametrack.back.model.SessaoDTO;
import gametrack.back.services.AutenticacaoService;

/**
 * Recebe requisições de login e delega ao serviço.
 *
 * @see RF002
 */
public class SessaoController {

    private final AutenticacaoService autenticacaoService;

    /**
     * Constrói o controlador injetando o serviço de autenticação.
     *
     * @param autenticacaoService serviço com as regras de autenticação
     */
    public SessaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    /**
     * Processa a solicitação de login.
     *
     * @param credenciais apelido e senha
     * @return sessão aberta
     */
    public SessaoDTO autenticar(CredenciaisDTO credenciais) {
        return autenticacaoService.autenticar(credenciais);
    }
}
