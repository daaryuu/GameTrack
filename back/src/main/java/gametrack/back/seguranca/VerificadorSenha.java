package gametrack.back.seguranca;

/**
 * Define o contrato para conferir uma senha com o seu hash.
 *
 * @see RF002
 * @see RNF006
 */
public interface VerificadorSenha {

    /**
     * Verifica se a senha corresponde ao hash.
     *
     * @param senhaPlana senha digitada pelo usuário
     * @param hash hash gravado na conta
     * @return verdadeiro se a senha corresponder
     */
    boolean eSenhaCorreta(String senhaPlana, String hash);
}
