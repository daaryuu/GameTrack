package gametrack.front.view;

import gametrack.front.cliente.UsuarioClienteFalso;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Abre a tela de cadastro de conta com dados em memória, para conferência visual.
 *
 * @see RF001
 */
public final class DemonstracaoCadastroConta {

    private static final Dimension TAMANHO = new Dimension(520, 460);

    private DemonstracaoCadastroConta() {
    }

    /**
     * Abre a janela.
     *
     * @param args não utilizado
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("GameTrack - Criar conta");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(new CadastroContaView(new UsuarioClienteFalso()));
            janela.setSize(TAMANHO);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
