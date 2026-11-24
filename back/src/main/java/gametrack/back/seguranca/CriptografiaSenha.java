package gametrack.back.seguranca;

/**
 * Define o contrato para geração de hash irreversível de senhas.
 *
 * @see RNF006
 */
public interface CriptografiaSenha {

    /**
     * Gera um hash seguro a partir da senha em texto plano.
     *
     * @param senhaPlana senha digitada pelo usuário
     * @return hash gerado
     */
    String gerarHash(String senhaPlana);
}