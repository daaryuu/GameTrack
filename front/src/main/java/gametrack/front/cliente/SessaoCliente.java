package gametrack.front.cliente;

import gametrack.front.dto.CredenciaisDTO;
import gametrack.front.dto.Perfil;

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
     * @return perfil da conta autenticada
     * @throws ErroServidorException se as credenciais forem recusadas ou o servidor falhar
     */
    Perfil autenticar(CredenciaisDTO credenciais);
}
