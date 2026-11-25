package gametrack.front.dto;

/**
 * Credenciais informadas na tela de login.
 *
 * @param apelido nome de usuário
 * @param senha   senha digitada
 * @see RF002
 */
public record CredenciaisDTO(String apelido, String senha) {
}
