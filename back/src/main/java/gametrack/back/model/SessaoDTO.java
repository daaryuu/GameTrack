package gametrack.back.model;

/**
 * Transporta a sessão criada no login.
 *
 * @param token identificador secreto da sessão, enviado pelo cliente nas próximas requisições
 * @see RF002
 */
public record SessaoDTO(String token) {
}
