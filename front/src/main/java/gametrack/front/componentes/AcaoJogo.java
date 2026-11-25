package gametrack.front.componentes;

import java.awt.Color;

/**
 * Ações oferecidas em cada linha da {@link TabelaJogos}.
 *
 * @see RF004
 * @see RF005
 * @see RF006
 */
public enum AcaoJogo {

    VER_DETALHES("Ver detalhes", null, null, Tema.LINK),
    ALTERAR("Alterar jogo", Tema.FUNDO_BOTAO_SECUNDARIO, Tema.BORDA, Tema.LINK),
    EXCLUIR("X", Tema.PERIGO, Tema.PERIGO, Tema.TEXTO);

    private final String texto;
    private final Color fundo;
    private final Color borda;
    private final Color corTexto;

    AcaoJogo(String texto, Color fundo, Color borda, Color corTexto) {
        this.texto = texto;
        this.fundo = fundo;
        this.borda = borda;
        this.corTexto = corTexto;
    }

    String texto() {
        return texto;
    }

    /** Diz se a ação é desenhada como botão; as demais são links, só com o texto. */
    boolean eBotao() {
        return fundo != null;
    }

    Color fundo() {
        return fundo;
    }

    Color borda() {
        return borda;
    }

    Color corTexto() {
        return corTexto;
    }
}
