package gametrack.back.seguranca;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Implementação de criptografia de senhas utilizando o algoritmo bcrypt.
 *
 * @see RNF006
 */
public class BcryptCriptografiaSenha implements CriptografiaSenha {

    private static final int CUSTO_LOGARITMICO = 12;

    /**
     * Gera o hash bcrypt da senha informada com salt aleatório.
     *
     * @param senhaPlana senha em texto plano
     * @return hash bcrypt pronto para armazenamento
     */
    @Override
    public String gerarHash(String senhaPlana) {
        return BCrypt.withDefaults().hashToString(CUSTO_LOGARITMICO, senhaPlana.toCharArray());
    }
}