package gametrack.front.view;

import gametrack.front.cliente.UsuarioCliente;
import gametrack.front.cliente.UsuarioClienteFalso;
import gametrack.front.cliente.UsuarioClienteHttp;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Abre a tela de cadastro de conta para conferência visual. Sem argumentos, usa dados em
 * memória. Com o endereço do servidor como primeiro argumento (por exemplo,
 * http://localhost:8080), fala com o servidor de verdade.
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
     * @param args opcionalmente, o endereço do servidor
     */
    public static void main(String[] args) {
        UsuarioCliente cliente = args.length > 0
                ? new UsuarioClienteHttp(args[0]) : new UsuarioClienteFalso();
        SwingUtilities.invokeLater(() -> abrirJanela(cliente));
    }

    private static void abrirJanela(UsuarioCliente cliente) {
        JFrame janela = new JFrame("GameTrack - Criar conta");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setContentPane(new CadastroContaView(cliente));
        janela.setSize(TAMANHO);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}
