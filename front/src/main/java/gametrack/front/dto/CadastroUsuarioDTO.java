package gametrack.front.dto;

/**
 * Dados informados no cadastro de conta.
 *
 * @param apelido nome de usuário
 * @param senha   senha digitada
 * @see RF001
 */
public record CadastroUsuarioDTO(String apelido, String senha) {
}
