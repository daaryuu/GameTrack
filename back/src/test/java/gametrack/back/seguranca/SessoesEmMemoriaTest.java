package gametrack.back.seguranca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/**
 * Testes da criação, validação e expiração das sessões.
 *
 * @see RF002
 * @see RNF007
 */
class SessoesEmMemoriaTest {

    private final RelogioFalso relogio = new RelogioFalso();
    private final SessoesEmMemoria sessoes =
            new SessoesEmMemoria(Duration.ofMinutes(30), relogio);

    @Test
    void deveIdentificarOUsuarioDaSessao() {
        String primeira = sessoes.abrir(7);
        String segunda = sessoes.abrir(8);

        assertNotEquals(primeira, segunda);
        assertEquals(7, sessoes.exigirUsuarioAutenticado("Bearer " + primeira));
        assertEquals(8, sessoes.exigirUsuarioAutenticado("Bearer " + segunda));
    }

    @Test
    void deveRecusarCredencialAusenteOuDesconhecida() {
        String token = sessoes.abrir(7);

        conferirRecusa(null);
        conferirRecusa("");
        conferirRecusa(token);
        conferirRecusa("Bearer ");
        conferirRecusa("Bearer outro");
    }

    @Test
    void deveExpirarDepoisDe30MinutosSemUso() {
        String credencial = "Bearer " + sessoes.abrir(7);
        relogio.avancar(Duration.ofMinutes(30));

        conferirRecusa(credencial);
    }

    @Test
    void deveRenovarOTempoACadaUso() {
        String credencial = "Bearer " + sessoes.abrir(7);
        relogio.avancar(Duration.ofMinutes(29));
        sessoes.exigirUsuarioAutenticado(credencial);
        relogio.avancar(Duration.ofMinutes(29));

        assertEquals(7, sessoes.exigirUsuarioAutenticado(credencial));
    }

    private void conferirRecusa(String credencial) {
        assertThrows(AutenticacaoException.class,
                () -> sessoes.exigirUsuarioAutenticado(credencial));
    }

    private static final class RelogioFalso extends Clock {

        private Instant agora = Instant.parse("2026-11-25T12:00:00Z");

        void avancar(Duration tempo) {
            agora = agora.plus(tempo);
        }

        @Override
        public Instant instant() {
            return agora;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zona) {
            return this;
        }
    }
}
