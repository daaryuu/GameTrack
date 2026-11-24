package gametrack.front.componentes;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.DefaultListCellRenderer;

/**
 * Caixa de opções escura de cantos arredondados. A primeira opção é o valor padrão
 * ("Todos", "Todas"…), que significa "sem filtro".
 */
public class CaixaOpcoes extends JComboBox<String> {

    private static final int ARCO = 16;
    private static final int PREENCHIMENTO_VERTICAL = 6;
    private static final int PREENCHIMENTO_HORIZONTAL = 12;
    private static final float TAMANHO_FONTE = 13f;
    private static final int METADE_SETA = 3;

    /** Cria a caixa, sem opções. */
    public CaixaOpcoes() {
        setOpaque(false);
        setForeground(Tema.TEXTO);
        setFont(Tema.fonte(Font.PLAIN, TAMANHO_FONTE));
        setRenderer(new RenderizadorOpcao());
        setUI(new InterfaceCaixa());
        setBorder(BorderFactory.createEmptyBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Substitui as opções e seleciona a padrão.
     *
     * @param padrao  texto da opção que significa "sem filtro"
     * @param valores demais opções, na ordem em que devem aparecer
     */
    public void definirOpcoes(String padrao, List<String> valores) {
        removeAllItems();
        addItem(padrao);
        valores.forEach(this::addItem);
        setSelectedIndex(0);
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(Tema.FUNDO_CAMPO);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARCO, ARCO));
        g2.dispose();
        super.paintComponent(grafico);
        Graphics2D borda = Tema.suavizar(grafico);
        borda.setColor(Tema.BORDA);
        borda.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1,
                ARCO, ARCO));
        borda.dispose();
    }

    /** Desenho do item exibido e dos itens da lista. */
    private static class RenderizadorOpcao extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor,
                int indice, boolean selecionado, boolean comFoco) {
            JLabel rotulo = (JLabel) super.getListCellRendererComponent(lista, valor, indice,
                    selecionado, false);
            rotulo.setBorder(BorderFactory.createEmptyBorder(PREENCHIMENTO_VERTICAL,
                    PREENCHIMENTO_HORIZONTAL, PREENCHIMENTO_VERTICAL, PREENCHIMENTO_HORIZONTAL));
            rotulo.setForeground(Tema.TEXTO);
            rotulo.setOpaque(indice >= 0);
            rotulo.setBackground(selecionado ? Tema.SELECAO : Tema.FUNDO_CARTAO);
            return rotulo;
        }
    }

    /** Interface gráfica sem fundo próprio, com seta e lista escuras. */
    private static class InterfaceCaixa extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {
            return new SetaCaixa();
        }

        @Override
        public void paintCurrentValueBackground(Graphics grafico, Rectangle area,
                boolean comFoco) {
            // O fundo é desenhado pela própria caixa.
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup lista = new BasicComboPopup(comboBox);
            lista.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
            return lista;
        }
    }

    /** Botão da seta, desenhado como um "v" fino. */
    private static class SetaCaixa extends JButton {

        SetaCaixa() {
            setBorder(BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);
            setFocusable(false);
            setOpaque(false);
        }

        @Override
        public void paint(Graphics grafico) {
            Graphics2D g2 = Tema.suavizar(grafico);
            g2.setColor(Tema.TEXTO_SECUNDARIO);
            g2.setStroke(new BasicStroke(1.6f));
            int centroX = getWidth() / 2;
            int centroY = getHeight() / 2;
            g2.drawLine(centroX - METADE_SETA, centroY - 1, centroX, centroY + 2);
            g2.drawLine(centroX, centroY + 2, centroX + METADE_SETA, centroY - 1);
            g2.dispose();
        }
    }
}
