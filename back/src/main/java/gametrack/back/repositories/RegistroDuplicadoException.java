package gametrack.back.repositories;

/**
 * Exceção lançada quando o banco recusa um registro por violar uma chave única.
 *
 * @see RF001.1
 */
public class RegistroDuplicadoException extends PersistenciaException {

    /**
     * Constrói a exceção preservando a causa original da falha no banco.
     *
     * @param causa exceção original lançada pelo driver JDBC
     */
    public RegistroDuplicadoException(Throwable causa) {
        super(causa);
    }
}
