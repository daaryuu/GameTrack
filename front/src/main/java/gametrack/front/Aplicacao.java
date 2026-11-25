package gametrack.front;

import gametrack.front.cliente.ConexaoHttp;
import gametrack.front.cliente.JogoCliente;
import gametrack.front.cliente.JogoClienteFalso;
import gametrack.front.cliente.JogoClienteHttp;
import gametrack.front.view.CatalogoListaView;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada da interface do GameTrack.
 * Sem argumentos, usa jogos em memória. Com o endereço do servidor como primeiro argumento
 * (por exemplo, http://localhost:8080), fala com o servidor de verdade.
 */
public final class Aplicacao {

    private static final String TITULO_JANELA = "GameTrack - Catálogo";
    private static final int LARGURA_JANELA = 880;
    private static final int ALTURA_JANELA = 700;
    private static final int LARGURA_MINIMA = 720;
    private static final int ALTURA_MINIMA = 520;

    private Aplicacao() {
    }

    /**
     * Abre a janela do catálogo.
     *
     * @param args opcionalmente, o endereço do servidor
     */
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        JogoCliente cliente = criarCliente(args);
        SwingUtilities.invokeLater(() -> abrirJanela(cliente));
    }

    private static JogoCliente criarCliente(String[] args) {
        return args.length > 0
                ? new JogoClienteHttp(new ConexaoHttp(args[0])) : new JogoClienteFalso();
    }

    private static void abrirJanela(JogoCliente cliente) {
        JFrame janela = new JFrame(TITULO_JANELA);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setContentPane(new CatalogoListaView(cliente));
        janela.setSize(LARGURA_JANELA, ALTURA_JANELA);
        janela.setMinimumSize(new Dimension(LARGURA_MINIMA, ALTURA_MINIMA));
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
    }
}
