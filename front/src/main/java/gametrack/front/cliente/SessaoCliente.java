package gametrack.front.cliente;

import gametrack.front.dto.CredenciaisDTO;

/**
 * Operações de sessão oferecidas pelo servidor.
 *
 * @see RF002
 */
public interface SessaoCliente {

    /**
     * Autentica o usuário.
     *
     * @param credenciais apelido e senha informados
     * @throws ErroServidorException se as credenciais forem recusadas ou o servidor falhar
     */
    void autenticar(CredenciaisDTO credenciais);
}
