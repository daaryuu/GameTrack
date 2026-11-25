package gametrack.back.repositories;

import gametrack.back.model.Perfil;
import java.util.Optional;

/**
 * Contrato de consulta do perfil das contas, usado para conferir permissões.
 *
 * @see RF003
 */
public interface PerfilRepository {

    /**
     * Busca o perfil de uma conta.
     *
     * @param usuarioId identificador da conta
     * @return perfil da conta, ou vazio se ela não existir
     */
    Optional<Perfil> buscarPerfil(int usuarioId);
}
