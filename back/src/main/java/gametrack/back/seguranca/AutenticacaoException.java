package gametrack.back.seguranca;

import gametrack.back.config.Mensagens;

/**
 * Exceção lançada quando uma operação restrita é invocada sem usuário autenticado.
 *
 * @see RF004
 */
public class AutenticacaoException extends RuntimeException {

    /**
     * Constrói a exceção com a mensagem padrão de falta de autenticação.
     */
    public AutenticacaoException() {
        super(Mensagens.USUARIO_NAO_AUTENTICADO);
    }
}