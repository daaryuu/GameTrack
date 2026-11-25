package gametrack.front.componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * Texto do tema escuro, alinhado à esquerda.
 */
public class Rotulo extends JLabel {

    private static final float TAMANHO_PADRAO = 12f;

    /**
     * Cria o rótulo no tamanho padrão, sem negrito.
     *
     * @param texto texto exibido
     * @param cor cor do texto
     */
    public Rotulo(String texto, Color cor) {
        super(texto);
        setForeground(cor);
        setFont(Tema.fonte(Font.PLAIN, TAMANHO_PADRAO));
        setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Cria um rótulo em negrito, para títulos.
     *
     * @param texto texto exibido
     * @param tamanho tamanho da fonte, em pontos
     */
    public Rotulo(String texto, float tamanho) {
        this(texto, Tema.TEXTO);
        setFont(Tema.fonte(Font.BOLD, tamanho));
    }
}
