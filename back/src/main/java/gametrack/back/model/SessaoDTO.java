package gametrack.back.model;

/**
 * Transporta a sessão criada no login.
 *
 * @param token identificador secreto da sessão, enviado pelo cliente nas próximas requisições
 * @param perfil perfil da conta, para o cliente escolher as telas
 * @see RF002
 */
public record SessaoDTO(String token, Perfil perfil) {
}
