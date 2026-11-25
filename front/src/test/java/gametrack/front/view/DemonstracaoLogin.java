package gametrack.front.view;

import gametrack.front.cliente.ConexaoHttp;
import gametrack.front.cliente.SessaoCliente;
import gametrack.front.cliente.SessaoClienteHttp;
import gametrack.front.cliente.UsuarioCliente;
import gametrack.front.cliente.UsuarioClienteFalso;
import gametrack.front.cliente.UsuarioClienteHttp;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Abre a tela de login para conferência visual. O link leva ao cadastro, que volta ao login
 * ao terminar ou pelo próprio link. Sem argumentos, usa contas em memória. Com o endereço do
 * servidor como primeiro argumento (por exemplo, http://localhost:8080), fala com o servidor
 * de verdade.
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
     * @param args opcionalmente, o endereço do servidor
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("GameTrack - Entrar");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setContentPane(args.length > 0 ? criarTelasHttp(args[0]) : criarTelasFalsas());
            janela.setSize(TAMANHO);
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }

    private static JPanel criarTelasHttp(String urlBase) {
        ConexaoHttp conexao = new ConexaoHttp(urlBase);
        return criarTelas(new UsuarioClienteHttp(conexao), new SessaoClienteHttp(conexao));
    }

    private static JPanel criarTelasFalsas() {
        UsuarioClienteFalso cliente = new UsuarioClienteFalso();
        return criarTelas(cliente, cliente);
    }

    static JPanel criarTelas(UsuarioCliente usuarios, SessaoCliente sessoes) {
        CardLayout layout = new CardLayout();
        JPanel telas = new JPanel(layout);
        LoginView login = new LoginView(sessoes);
        CadastroContaView cadastro = new CadastroContaView(usuarios);
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
