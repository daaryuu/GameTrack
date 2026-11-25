package gametrack.front.dto;

/**
 * Sessão criada no login.
 *
 * @param token identificador secreto da sessão, enviado nas próximas requisições
 * @param perfil perfil da conta autenticada
 * @see RF002
 */
public record SessaoDTO(String token, Perfil perfil) {
}
