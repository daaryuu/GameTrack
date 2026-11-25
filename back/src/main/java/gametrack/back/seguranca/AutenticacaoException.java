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
        this(Mensagens.USUARIO_NAO_AUTENTICADO);
    }

    /**
     * Constrói a exceção com uma mensagem específica, como a de credenciais incorretas.
     *
     * @param mensagem mensagem de erro para exibição ao usuário
     */
    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}