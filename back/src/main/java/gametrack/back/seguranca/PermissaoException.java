package gametrack.back.seguranca;

import gametrack.back.config.Mensagens;

/**
 * Exceção lançada quando um usuário autenticado tenta uma operação exclusiva do administrador.
 *
 * @see RF003
 */
public class PermissaoException extends RuntimeException {

    /**
     * Constrói a exceção com a mensagem padrão de falta de permissão.
     */
    public PermissaoException() {
        super(Mensagens.EXCLUSIVO_ADMINISTRADOR);
    }
}
