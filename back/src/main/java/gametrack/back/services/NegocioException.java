package gametrack.back.services;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 *
 * @see RF001
 */
public class NegocioException extends RuntimeException {

    /**
     * Constrói a exceção com a mensagem exata especificada nos requisitos.
     *
     * @param mensagem mensagem de erro para exibição ao usuário
     */
    public NegocioException(String mensagem) {
        super(mensagem);
    }
}