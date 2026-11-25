package gametrack.back.seguranca;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Testes da conferência de senha com bcrypt.
 *
 * @see RNF006
 */
class BcryptCriptografiaSenhaTest {

    private final BcryptCriptografiaSenha criptografia = new BcryptCriptografiaSenha();

    @Test
    void deveConferirASenhaComOHash() {
        String hash = criptografia.gerarHash("çenha1234");

        assertTrue(criptografia.eSenhaCorreta("çenha1234", hash));
        assertFalse(criptografia.eSenhaCorreta("Çenha1234", hash));
    }

    @Test
    void deveRecusarSenhaAcimaDoLimiteSemLancarErro() {
        String hash = criptografia.gerarHash("a".repeat(71));

        assertTrue(criptografia.eSenhaCorreta("a".repeat(71), hash));
        assertFalse(criptografia.eSenhaCorreta("a".repeat(72), hash));
    }
}
