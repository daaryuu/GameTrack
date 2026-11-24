package gametrack.back.repositories;

import gametrack.back.config.Mensagens;

/**
 * Exceção lançada quando ocorre falha ou timeout na camada de persistência.
 *
 * @see RNF003
 */
public class PersistenciaException extends RuntimeException {

    /**
     * Constrói a exceção preservando a causa original da falha no banco.
     *
     * @param causa exceção original lançada pelo driver JDBC
     */
    public PersistenciaException(Throwable causa) {
        super(Mensagens.ERRO_COMUNICACAO_BANCO, causa);
    }
}