package gametrack.front.view;

import gametrack.front.cliente.UsuarioClienteFalso;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Abre a tela de login com contas em memória, para conferência visual. O link leva ao
 * cadastro, que volta ao login ao terminar ou pelo próprio link.
 *
 * @see RF001
 * @see RF002
 */
public final class DemonstracaoLogin {

    private static final Dimension TAMANHO = new Dimension(520, 600);
    private static final String LOGIN = "login";
    private static final String CADASTRO = "cadastro";

    private DemonstracaoLogin() {
    }

    /**
     * Abre a janela.
     *
     * @param args não utilizado
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("GameTrack - Entrar");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(criarTelas(new UsuarioClienteFalso()));
            janela.setSize(TAMANHO);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }

    static JPanel criarTelas(UsuarioClienteFalso cliente) {
        CardLayout layout = new CardLayout();
        JPanel telas = new JPanel(layout);
        LoginView login = new LoginView(cliente);
        CadastroContaView cadastro = new CadastroContaView(cliente);
        telas.add(login, LOGIN);
        telas.add(cadastro, CADASTRO);
        login.definirAoCadastrar(() -> layout.show(telas, CADASTRO));
        login.definirAoEntrar(() -> JOptionPane.showMessageDialog(telas, "Autenticado."));
        cadastro.definirAoVoltar(() -> layout.show(telas, LOGIN));
        cadastro.definirAoCadastrar(() -> {
            login.exibirSucesso("Conta cadastrada com sucesso!");
            layout.show(telas, LOGIN);
        });
        return telas;
    }
}
