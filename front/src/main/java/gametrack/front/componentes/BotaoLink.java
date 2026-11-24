package gametrack.front.componentes;

import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Botão com aparência de link: só o texto colorido, sublinhado ao passar o mouse.
 */
public class BotaoLink extends JButton {

    private static final float TAMANHO_FONTE = 13f;
    private static final int FOLGA_SUBLINHADO = 2;

    /**
     * Cria o link.
     *
     * @param texto texto exibido
     */
    public BotaoLink(String texto) {
        super(texto);
        setForeground(Tema.LINK);
        setFont(Tema.fonte(java.awt.Font.PLAIN, TAMANHO_FONTE));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setBorder(BorderFactory.createEmptyBorder(FOLGA_SUBLINHADO, FOLGA_SUBLINHADO,
                FOLGA_SUBLINHADO, FOLGA_SUBLINHADO));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        super.paintComponent(grafico);
        if (getModel().isRollover()) {
            FontMetrics medidas = getFontMetrics(getFont());
            int largura = medidas.stringWidth(getText());
            int x = (getWidth() - largura) / 2;
            int y = (getHeight() + medidas.getAscent() - medidas.getDescent()) / 2
                    + FOLGA_SUBLINHADO;
            grafico.setColor(getForeground());
            grafico.drawLine(x, y, x + largura, y);
        }
    }
}
