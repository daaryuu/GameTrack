package gametrack.back.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

/**
 * Configuração do servidor, lida do arquivo {@code config.properties} (que o Git ignora).
 * O modelo, sem senhas, é {@code config.example.properties}.
 *
 * @see RNF003
 */
public final class Configuracao {

    private static final String ARQUIVO = "config.properties";
    private static final String EXPIRACAO_PADRAO_MINUTOS = "30";

    private final Properties propriedades;

    private Configuracao(Properties propriedades) {
        this.propriedades = propriedades;
    }

    /**
     * Lê o {@code config.properties} do classpath.
     *
     * @return a configuração carregada
     * @throws IllegalStateException se o arquivo não existir ou não puder ser lido
     */
    public static Configuracao carregar() {
        try (InputStream entrada = Configuracao.class.getClassLoader()
                .getResourceAsStream(ARQUIVO)) {
            if (entrada == null) {
                throw new IllegalStateException(Mensagens.CONFIGURACAO_NAO_ENCONTRADA);
            }
            return ler(entrada);
        } catch (IOException e) {
            throw new IllegalStateException(Mensagens.CONFIGURACAO_NAO_ENCONTRADA, e);
        }
    }

    /**
     * Lê a configuração de um fluxo de propriedades.
     *
     * @param entrada conteúdo no formato de arquivo .properties
     * @return a configuração carregada
     * @throws IOException se o fluxo não puder ser lido
     */
    public static Configuracao ler(InputStream entrada) throws IOException {
        Properties propriedades = new Properties();
        propriedades.load(new InputStreamReader(entrada, StandardCharsets.UTF_8));
        return new Configuracao(propriedades);
    }

    /**
     * Devolve a URL JDBC do banco.
     *
     * @return valor de {@code db.url}
     */
    public String urlBanco() {
        return obrigatoria("db.url");
    }

    /**
     * Devolve o usuário do banco.
     *
     * @return valor de {@code db.usuario}
     */
    public String usuarioBanco() {
        return obrigatoria("db.usuario");
    }

    /**
     * Devolve a senha do banco.
     *
     * @return valor de {@code db.senha}, que pode ser vazio
     */
    public String senhaBanco() {
        return propriedades.getProperty("db.senha", "");
    }

    /**
     * Devolve a porta em que o servidor escuta.
     *
     * @return valor de {@code servidor.porta}
     */
    public int portaServidor() {
        return Integer.parseInt(obrigatoria("servidor.porta"));
    }

    /**
     * Devolve o tempo sem uso até a sessão expirar (RNF007).
     *
     * @return valor de {@code sessao.expiracao.minutos}, ou 30 minutos se não for informado
     */
    public Duration expiracaoDaSessao() {
        return Duration.ofMinutes(Long.parseLong(propriedades.getProperty(
                "sessao.expiracao.minutos", EXPIRACAO_PADRAO_MINUTOS).trim()));
    }

    /**
     * Devolve a senha inicial da conta do administrador.
     *
     * @return valor de {@code admin.senha}
     */
    public String senhaAdministrador() {
        return obrigatoria("admin.senha");
    }

    private String obrigatoria(String chave) {
        String valor = propriedades.getProperty(chave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(Mensagens.CONFIGURACAO_INCOMPLETA + chave);
        }
        return valor.trim();
    }
}
