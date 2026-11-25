package gametrack.front.componentes;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.text.JTextComponent;

/**
 * Aparência comum aos campos de digitação: fundo escuro, cantos arredondados, dica exibida
 * enquanto o campo está vazio e contorno roxo quando está em foco.
 */
final class EstiloCampo {

    private static final int ARCO = 16;
    private static final int PREENCHIMENTO_VERTICAL = 9;
    private static final int PREENCHIMENTO_HORIZONTAL = 12;
    private static final float TAMANHO_FONTE = 13f;

    private EstiloCampo() {
    }

    static void aplicar(JTextComponent campo) {
        campo.setOpaque(false);
        campo.setBackground(Tema.FUNDO_CAMPO);
        campo.setForeground(Tema.TEXTO);
        campo.setCaretColor(Tema.TEXTO);
        campo.setSelectionColor(Tema.ROXO);
        campo.setFont(Tema.fonte(Font.PLAIN, TAMANHO_FONTE));
        campo.setBorder(BorderFactory.createEmptyBorder(PREENCHIMENTO_VERTICAL,
                PREENCHIMENTO_HORIZONTAL, PREENCHIMENTO_VERTICAL, PREENCHIMENTO_HORIZONTAL));
    }

    static void desenharFundo(JTextComponent campo, Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(campo.getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, campo.getWidth(), campo.getHeight(),
                ARCO, ARCO));
        g2.dispose();
    }

    static void desenharDica(JTextComponent campo, Graphics grafico, String dica) {
        if (campo.getDocument().getLength() > 0) {
            return;
        }
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(Tema.TEXTO_SECUNDARIO);
        g2.setFont(campo.getFont());
        int base = (campo.getHeight() + g2.getFontMetrics().getAscent()
                - g2.getFontMetrics().getDescent()) / 2;
        g2.drawString(dica, campo.getInsets().left, base);
        g2.dispose();
    }

    static void desenharContorno(JTextComponent campo, Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(campo.hasFocus() ? Tema.ROXO : Tema.BORDA);
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, campo.getWidth() - 1,
                campo.getHeight() - 1, ARCO, ARCO));
        g2.dispose();
    }
}
