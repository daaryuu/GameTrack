package gametrack.back.seguranca;

import at.favre.lib.crypto.bcrypt.BCrypt;
import gametrack.back.config.Mensagens;
import java.nio.charset.StandardCharsets;

/**
 * Implementação de criptografia de senhas utilizando o algoritmo bcrypt.
 *
 * @see RNF006
 */
public class BcryptCriptografiaSenha implements CriptografiaSenha, VerificadorSenha {

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

    /**
     * Confere a senha com o hash bcrypt. Uma senha acima do limite do bcrypt nunca confere.
     *
     * @param senhaPlana senha em texto plano
     * @param hash hash bcrypt gravado
     * @return verdadeiro se a senha corresponder ao hash
     */
    @Override
    public boolean eSenhaCorreta(String senhaPlana, String hash) {
        if (senhaPlana.getBytes(StandardCharsets.UTF_8).length
                > Mensagens.TAMANHO_MAXIMO_SENHA_BYTES) {
            return false;
        }
        return BCrypt.verifyer().verify(senhaPlana.toCharArray(), hash).verified;
    }
}