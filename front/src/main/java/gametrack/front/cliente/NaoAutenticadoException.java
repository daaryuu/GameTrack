package gametrack.front.cliente;

/**
 * Exceção lançada quando o servidor recusa a requisição por falta de autenticação, como
 * depois que a sessão expira, ou recusa as credenciais do login.
 *
 * @see RF002
 * @see RNF007
 */
public class NaoAutenticadoException extends ErroServidorException {

    /**
     * Constrói a exceção com a mensagem enviada pelo servidor.
     *
     * @param mensagem mensagem de erro para exibição ao usuário
     */
    public NaoAutenticadoException(String mensagem) {
        super(mensagem);
    }
}
