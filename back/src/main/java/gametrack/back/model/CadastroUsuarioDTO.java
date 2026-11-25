package gametrack.back.model;

/**
 * Transporta os dados de entrada para o cadastro de uma nova conta.
 *
 * @param apelido nome único escolhido pelo usuário
 * @param senha senha em texto plano enviada pelo cliente
 * @see RF001
 */
public record CadastroUsuarioDTO(String apelido, String senha) {
}