package gametrack.front.dto;

/**
 * Sessão criada no login.
 *
 * @param token identificador secreto da sessão, enviado nas próximas requisições
 * @see RF002
 */
public record SessaoDTO(String token) {
}
