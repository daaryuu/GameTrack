package gametrack.front.view;

import gametrack.front.componentes.BotaoLink;
import gametrack.front.componentes.Tema;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Linha centralizada com um convite e um link, usada no rodapé das telas de conta.
 *
 * @see RF001
 * @see RF002
 */
class ConviteComLink extends JPanel {

    private static final int ESPACO = 3;
    private static final float TAMANHO_FONTE = 12f;

    private final BotaoLink link;

    ConviteComLink(String convite, String textoLink) {
        super(new FlowLayout(FlowLayout.CENTER, ESPACO, 0));
        this.link = new BotaoLink(textoLink);
        setOpaque(false);
        JLabel rotulo = new JLabel(convite);
        rotulo.setFont(Tema.fonte(Font.PLAIN, TAMANHO_FONTE));
        rotulo.setForeground(Tema.TEXTO_SECUNDARIO);
        link.setFont(Tema.fonte(Font.BOLD, TAMANHO_FONTE));
        add(rotulo);
        add(link);
    }

    void definirAoClicar(Runnable acao) {
        link.addActionListener(evento -> acao.run());
    }
}
