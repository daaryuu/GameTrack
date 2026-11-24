package gametrack.front.componentes;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Campo de texto escuro de cantos arredondados, com uma dica exibida enquanto está vazio.
 * O contorno fica roxo quando o campo está em foco.
 */
public class CampoTextoComDica extends JTextField {

    private static final int ARCO = 16;
    private static final int PREENCHIMENTO_VERTICAL = 9;
    private static final int PREENCHIMENTO_HORIZONTAL = 12;
    private static final float TAMANHO_FONTE = 13f;

    private final String dica;

    /**
     * Cria o campo.
     *
     * @param dica texto de ajuda exibido quando o campo está vazio
     */
    public CampoTextoComDica(String dica) {
        this.dica = dica;
        setOpaque(false);
        setForeground(Tema.TEXTO);
        setCaretColor(Tema.TEXTO);
        setSelectionColor(Tema.ROXO);
        setFont(Tema.fonte(Font.PLAIN, TAMANHO_FONTE));
        setBorder(BorderFactory.createEmptyBorder(PREENCHIMENTO_VERTICAL,
                PREENCHIMENTO_HORIZONTAL, PREENCHIMENTO_VERTICAL, PREENCHIMENTO_HORIZONTAL));
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(Tema.FUNDO_CAMPO);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARCO, ARCO));
        g2.dispose();
        super.paintComponent(grafico);
        desenharDica(grafico);
        desenharContorno(grafico);
    }

    private void desenharDica(Graphics grafico) {
        if (getText().isEmpty()) {
            Graphics2D g2 = Tema.suavizar(grafico);
            g2.setColor(Tema.TEXTO_SECUNDARIO);
            g2.setFont(getFont());
            int base = (getHeight() + g2.getFontMetrics().getAscent()
                    - g2.getFontMetrics().getDescent()) / 2;
            g2.drawString(dica, getInsets().left, base);
            g2.dispose();
        }
    }

    private void desenharContorno(Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(hasFocus() ? Tema.ROXO : Tema.BORDA);
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1,
                ARCO, ARCO));
        g2.dispose();
    }
}
