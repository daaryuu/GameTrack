package gametrack.back.services;

/**
 * Exceção lançada quando o registro pedido não existe.
 *
 * @see RF005
 */
public class NaoEncontradoException extends RuntimeException {

    /**
     * Constrói a exceção com a mensagem para o usuário.
     *
     * @param mensagem mensagem de erro para exibição ao usuário
     */
    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
