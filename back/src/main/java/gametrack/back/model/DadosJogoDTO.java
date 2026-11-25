package gametrack.back.model;

/**
 * Transporta os dados de um jogo enviados pelo administrador, ainda não validados.
 *
 * @param titulo título do jogo
 * @param genero gênero principal
 * @param desenvolvedora estúdio ou empresa desenvolvedora
 * @param ano ano de lançamento, nulo se não for informado
 * @see RF003
 */
public record DadosJogoDTO(String titulo, String genero, String desenvolvedora, Integer ano) {
}
