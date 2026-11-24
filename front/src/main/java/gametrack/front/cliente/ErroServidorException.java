package gametrack.front.cliente;

public class ErroServidorException extends RuntimeException {
    public ErroServidorException(String mensagem){
        super(mensagem);
    }

    public ErroServidorException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }
}
