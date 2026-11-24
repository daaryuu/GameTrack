package gametrack.back.config;

/**
 * Centraliza as mensagens exibidas ao usuário e parâmetros globais do sistema.
 *
 * @see RF001
 * @see RNF003
 * @see RNF005
 */
public final class Mensagens {

    public static final int TAMANHO_MINIMO_SENHA = 8;
    public static final int TIMEOUT_BANCO_SEGUNDOS = 5;

    public static final String CONTA_CADASTRADA_SUCESSO = "Conta cadastrada com sucesso!";
    public static final String SENHA_NAO_INFORMADA = "Informe a senha!";
    public static final String APELIDO_NAO_INFORMADO = "Informe o apelido!";
    public static final String APELIDO_JA_UTILIZADO = "Apelido já utilizado!";
    public static final String SENHA_TAMANHO_INVALIDO =
            "A senha deve ter no mínimo 8 caracteres!";
    public static final String ERRO_COMUNICACAO_BANCO =
            "Erro ao comunicar com o banco de dados.";

    private Mensagens() {
    }
}