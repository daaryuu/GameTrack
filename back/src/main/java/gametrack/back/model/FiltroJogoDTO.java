package gametrack.back.model;

/**
 * Transporta os critérios de filtro para consulta no catálogo de jogos.
 *
 * @param titulo trecho do título do jogo (opcional)
 * @param genero trecho ou nome do gênero (opcional)
 * @param desenvolvedora trecho ou nome da desenvolvedora (opcional)
 * @param ano ano de lançamento em texto (opcional)
 * @see RF004
 * @see RF004.1
 */
public record FiltroJogoDTO(String titulo, String genero, String desenvolvedora, String ano) {

    /**
     * Retorna um filtro vazio para listagem completa do catálogo.
     *
     * @return instância sem critérios preenchidos
     */
    public static FiltroJogoDTO semFiltro() {
        return new FiltroJogoDTO(null, null, null, null);
    }
}