package gametrack.front.componentes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Botão de cantos arredondados que clareia ao passar o mouse.
 * Use {@link Tema#ROXO} para o botão principal e {@link Tema#FUNDO_BOTAO_SECUNDARIO}
 * para os secundários.
 */
public class BotaoArredondado extends JButton {

    private static final int ARCO = 16;
    private static final int PREENCHIMENTO_VERTICAL = 9;
    private static final int PREENCHIMENTO_HORIZONTAL = 18;
    private static final float TAMANHO_FONTE = 13f;

    private final Color corFundo;

    /**
     * Cria o botão.
     *
     * @param texto    texto exibido
     * @param corFundo cor de fundo
     */
    public BotaoArredondado(String texto, Color corFundo) {
        super(texto);
        this.corFundo = corFundo;
        setForeground(Tema.TEXTO);
        setFont(Tema.fonte(Font.BOLD, TAMANHO_FONTE));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(PREENCHIMENTO_VERTICAL,
                PREENCHIMENTO_HORIZONTAL, PREENCHIMENTO_VERTICAL, PREENCHIMENTO_HORIZONTAL));
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(getModel().isRollover() ? Tema.clarear(corFundo) : corFundo);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARCO, ARCO));
        g2.dispose();
        super.paintComponent(grafico);
    }
}
