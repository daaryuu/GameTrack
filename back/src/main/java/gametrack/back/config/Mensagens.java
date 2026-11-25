package gametrack.back.config;

/**
 * Centraliza as mensagens exibidas ao usuário e parâmetros globais do sistema.
 *
 * @see RF001
 * @see RF004
 * @see RNF003
 * @see RNF004
 * @see RNF005
 */
public final class Mensagens {

    public static final int TAMANHO_MINIMO_SENHA = 8;
    /** Limite do bcrypt: 72 bytes, contando o terminador que a biblioteca acrescenta. */
    public static final int TAMANHO_MAXIMO_SENHA_BYTES = 71;
    public static final int TAMANHO_MINIMO_APELIDO = 3;
    public static final int TAMANHO_MAXIMO_APELIDO = 30;
    public static final int TIMEOUT_BANCO_SEGUNDOS = 5;
    public static final int LIMITE_MAXIMO_CATALOGO = 500;
    public static final int TAMANHO_MAXIMO_TITULO = 100;
    public static final int TAMANHO_MAXIMO_GENERO = 40;
    public static final int TAMANHO_MAXIMO_DESENVOLVEDORA = 80;
    public static final int ANO_MINIMO_JOGO = 1950;
    public static final int ANO_MAXIMO_JOGO = 2100;
    public static final String APELIDO_ADMINISTRADOR = "admin";

    public static final String CONTA_CADASTRADA_SUCESSO = "Conta cadastrada com sucesso!";
    public static final String SENHA_NAO_INFORMADA = "Informe a senha!";
    public static final String APELIDO_NAO_INFORMADO = "Informe o apelido!";
    public static final String APELIDO_JA_UTILIZADO = "Apelido já utilizado!";
    public static final String SENHA_TAMANHO_INVALIDO =
            "A senha deve ter no mínimo 8 caracteres!";
    public static final String SENHA_MUITO_LONGA = "A senha é longa demais!";
    public static final String APELIDO_TAMANHO_INVALIDO =
            "O apelido deve ter de 3 a 30 caracteres!";
    public static final String CORPO_INVALIDO = "Os dados enviados são inválidos.";
    public static final String ERRO_COMUNICACAO_BANCO =
            "Erro ao comunicar com o banco de dados.";

    public static final String CREDENCIAIS_INCORRETAS = "Apelido ou senha incorretos!";
    public static final String USUARIO_NAO_AUTENTICADO = "Usuário não autenticado.";
    public static final String EXCLUSIVO_ADMINISTRADOR =
            "Operação exclusiva do administrador.";
    public static final String JOGO_CADASTRADO_SUCESSO = "Jogo cadastrado com sucesso!";
    public static final String JOGO_ALTERADO_SUCESSO = "Jogo alterado com sucesso!";
    public static final String JOGO_EXCLUIDO_SUCESSO = "Jogo excluído com sucesso!";
    public static final String JOGO_NAO_ENCONTRADO = "Jogo não encontrado!";
    public static final String JOGO_DADOS_NAO_INFORMADOS = "Informe todos os dados do jogo!";
    public static final String JOGO_DADOS_LONGOS =
            "Use no máximo 100 caracteres no título, 40 no gênero e 80 na desenvolvedora!";
    public static final String JOGO_ANO_INVALIDO =
            "O ano de lançamento deve estar entre 1950 e 2100!";
    public static final String ANO_FILTRO_INVALIDO =
            "O ano informado no filtro deve ser numérico.";
    public static final String PARAMETROS_INVALIDOS = "Os parâmetros da consulta são inválidos.";
    public static final String METODO_NAO_PERMITIDO = "Método não permitido.";
    public static final String ERRO_INESPERADO = "Erro inesperado no servidor.";
    public static final String CONFIGURACAO_NAO_ENCONTRADA =
            "Arquivo config.properties não encontrado. Copie config.example.properties para "
                    + "config.properties e preencha os dados do banco.";
    public static final String APELIDO_ADMINISTRADOR_OCUPADO =
            "O apelido admin pertence a uma conta comum. Renomeie ou exclua essa conta no banco "
                    + "para o servidor criar o administrador.";
    public static final String CONFIGURACAO_INCOMPLETA =
            "Propriedade obrigatória ausente em config.properties: ";

    private Mensagens() {
    }
}