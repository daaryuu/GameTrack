package gametrack.back.model;

/**
 * Transporta as credenciais informadas no login.
 *
 * @param apelido apelido da conta
 * @param senha senha em texto plano enviada pelo cliente
 * @see RF002
 */
public record CredenciaisDTO(String apelido, String senha) {
}
