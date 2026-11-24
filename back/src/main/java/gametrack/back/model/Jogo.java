package gametrack.back.model;

/**
 * Representa a entidade de jogo do catálogo global no domínio do GameTrack.
 *
 * @see RF004
 */
public class Jogo {

    private Integer id;
    private String titulo;
    private String genero;
    private String desenvolvedora;
    private int ano;

    /**
     * Constrói uma instância de jogo do catálogo.
     *
     * @param id identificador único no banco
     * @param titulo título oficial do jogo
     * @param genero gênero principal do jogo
     * @param desenvolvedora estúdio ou empresa desenvolvedora
     * @param ano ano de lançamento
     */
    public Jogo(Integer id, String titulo, String genero, String desenvolvedora, int ano) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.desenvolvedora = desenvolvedora;
        this.ano = ano;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getGenero() {
        return genero;
    }

    public String getDesenvolvedora() {
        return desenvolvedora;
    }

    public int getAno() {
        return ano;
    }
}