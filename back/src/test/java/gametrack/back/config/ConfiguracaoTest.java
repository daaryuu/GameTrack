package gametrack.back.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Testes da leitura do arquivo de configuração do servidor.
 *
 * @see RNF003
 */
class ConfiguracaoTest {

    private static Configuracao de(String conteudo) throws IOException {
        byte[] bytes = conteudo.getBytes(StandardCharsets.UTF_8);
        return Configuracao.ler(new ByteArrayInputStream(bytes));
    }

    @Test
    void deveLerOsDadosDoBancoEAPortaDoServidor() throws IOException {
        Configuracao configuracao = de("""
                db.url=jdbc:mysql://localhost:3306/gametrack
                db.usuario=root
                db.senha=segredo
                servidor.porta=8080
                """);

        assertEquals("jdbc:mysql://localhost:3306/gametrack", configuracao.urlBanco());
        assertEquals("root", configuracao.usuarioBanco());
        assertEquals("segredo", configuracao.senhaBanco());
        assertEquals(8080, configuracao.portaServidor());
    }

    @Test
    void devePermitirSenhaDoBancoVazia() throws IOException {
        Configuracao configuracao = de("db.url=x\ndb.usuario=root\nservidor.porta=1\n");

        assertEquals("", configuracao.senhaBanco());
    }

    @Test
    void deveLerAExpiracaoDaSessaoOuUsar30Minutos() throws IOException {
        assertEquals(Duration.ofMinutes(5),
                de("sessao.expiracao.minutos=5\n").expiracaoDaSessao());
        assertEquals(Duration.ofMinutes(30), de("").expiracaoDaSessao());
    }

    @Test
    void deveInformarQualPropriedadeObrigatoriaEstaAusente() throws IOException {
        Configuracao configuracao = de("db.usuario=root\nservidor.porta=8080\n");

        IllegalStateException excecao =
                assertThrows(IllegalStateException.class, configuracao::urlBanco);

        assertTrue(excecao.getMessage().endsWith("db.url"));
    }
}
