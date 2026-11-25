package gametrack.back.model;

/**
 * Transporta os dados de saída de um jogo do catálogo para o cliente.
 *
 * @param id identificador único do jogo
 * @param titulo título do jogo
 * @param genero gênero do jogo
 * @param desenvolvedora desenvolvedora do jogo
 * @param ano ano de lançamento
 * @see RF004
 */
public record JogoDTO(Integer id, String titulo, String genero, String desenvolvedora, int ano) {

    /**
     * Converte uma entidade {@link Jogo} em seu respectivo DTO de transferência.
     *
     * @param jogo entidade de domínio
     * @return DTO preenchido com os dados do jogo
     */
    public static JogoDTO deEntidade(Jogo jogo) {
        return new JogoDTO(
                jogo.getId(),
                jogo.getTitulo(),
                jogo.getGenero(),
                jogo.getDesenvolvedora(),
                jogo.getAno());
    }
}