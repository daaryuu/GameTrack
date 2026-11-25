package gametrack.front;

import gametrack.front.cliente.ConexaoHttp;
import gametrack.front.cliente.JogoClienteFalso;
import gametrack.front.cliente.JogoClienteHttp;
import gametrack.front.cliente.SessaoClienteHttp;
import gametrack.front.cliente.UsuarioClienteFalso;
import gametrack.front.cliente.UsuarioClienteHttp;
import gametrack.front.view.PrincipalView;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da interface do GameTrack. Abre na tela de login.
 * Sem argumentos, usa contas e jogos em memória. Com o endereço do servidor como primeiro
 * argumento (por exemplo, http://localhost:8080), fala com o servidor de verdade.
 *
 * @see RF002
 */
public final class Aplicacao {

    private static final String TITULO_JANELA = "GameTrack";
    private static final int LARGURA_JANELA = 880;
    private static final int ALTURA_JANELA = 700;
    private static final int LARGURA_MINIMA = 720;
    private static final int ALTURA_MINIMA = 520;

    private Aplicacao() {
    }

    /**
     * Abre a janela na tela de login.
     *
     * @param args opcionalmente, o endereço do servidor
     */
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> abrirJanela(
                args.length > 0 ? criarTelasHttp(args[0]) : criarTelasEmMemoria()));
    }

    private static PrincipalView criarTelasHttp(String urlBase) {
        ConexaoHttp conexao = new ConexaoHttp(urlBase);
        return new PrincipalView(new UsuarioClienteHttp(conexao),
                new SessaoClienteHttp(conexao), new JogoClienteHttp(conexao));
    }

    private static PrincipalView criarTelasEmMemoria() {
        UsuarioClienteFalso contas = new UsuarioClienteFalso();
        return new PrincipalView(contas, contas, new JogoClienteFalso());
    }

    private static void abrirJanela(PrincipalView telas) {
        JFrame janela = new JFrame(TITULO_JANELA);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setContentPane(telas);
        janela.setSize(LARGURA_JANELA, ALTURA_JANELA);
        janela.setMinimumSize(new Dimension(LARGURA_MINIMA, ALTURA_MINIMA));
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}
