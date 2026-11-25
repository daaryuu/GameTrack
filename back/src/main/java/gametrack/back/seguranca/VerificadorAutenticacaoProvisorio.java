package gametrack.back.seguranca;

/**
 * Verificador provisório: considera toda requisição como feita por um usuário fixo.
 * Existe só para o catálogo poder ser usado antes de o login (RF002) estar pronto.
 * Deve ser trocado por uma implementação que valide a sessão (RNF007) assim que o login
 * existir, e não pode ser usado em produção.
 *
 * @see RF002
 */
public class VerificadorAutenticacaoProvisorio implements VerificadorAutenticacao {

    private static final int USUARIO_PROVISORIO_ID = 1;

    @Override
    public Integer exigirUsuarioAutenticado(String credencial) {
        return USUARIO_PROVISORIO_ID;
    }
}
