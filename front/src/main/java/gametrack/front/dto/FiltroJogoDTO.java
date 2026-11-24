package gametrack.front.dto;

public record FiltroJogoDTO(String titulo, String genero, String desenvolvedora, String ano) {

    public static FiltroJogoDTO semFiltro(){
        return new FiltroJogoDTO(null, null, null, null);
    }
}
