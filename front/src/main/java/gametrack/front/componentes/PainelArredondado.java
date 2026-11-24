package gametrack.front.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Painel com fundo e contorno de cantos arredondados, usado como cartão.
 * O conteúdo interno também é recortado nos cantos.
 */
public class PainelArredondado extends JPanel {

    private final Color corBorda;

    /**
     * Cria o cartão.
     *
     * @param fundo cor de fundo
     * @param borda cor do contorno
     */
    public PainelArredondado(Color fundo, Color borda) {
        super(new BorderLayout());
        this.corBorda = borda;
        setOpaque(false);
        setBackground(fundo);
    }

    private Shape contorno() {
        return new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1,
                Tema.RAIO * 2, Tema.RAIO * 2);
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(getBackground());
        g2.fill(contorno());
        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics grafico) {
        Graphics2D recortado = Tema.suavizar(grafico);
        recortado.clip(contorno());
        super.paintChildren(recortado);
        recortado.dispose();
        Graphics2D borda = Tema.suavizar(grafico);
        borda.setColor(corBorda);
        borda.draw(contorno());
        borda.dispose();
    }
}
