package gametrack.back.seguranca;

/**
 * Contrato para validação de sessão ou credencial de usuário autenticado.
 *
 * @see RF004
 */
public interface VerificadorAutenticacao {

    /**
     * Valida o token ou identificador de sessão recebido na requisição.
     *
     * @param credencial valor do cabeçalho de autenticação
     * @return identificador do usuário autenticado
     * @throws AutenticacaoException caso a credencial seja ausente ou inválida
     */
    Integer exigirUsuarioAutenticado(String credencial);
}