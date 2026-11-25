package gametrack.front.cliente;

import gametrack.front.dto.CadastroUsuarioDTO;

/**
 * Operações de conta oferecidas pelo servidor.
 *
 * @see RF001
 */
public interface UsuarioCliente {

    /**
     * Cadastra uma conta.
     *
     * @param dados apelido e senha informados
     * @throws ErroServidorException se os dados forem recusados ou o servidor falhar
     */
    void cadastrar(CadastroUsuarioDTO dados);
}
