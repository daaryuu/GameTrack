package gametrack.front.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Janela de mensagem no tema escuro, que bloqueia a tela até ser respondida. A tecla Esc
 * equivale a cancelar.
 *
 * @see RF006
 */
public final class Dialogo extends JDialog {

    private static final Color TRANSPARENTE = new Color(0, 0, 0, 0);
    private static final int MARGEM = 34;
    private static final int ESPACO = 14;
    private static final int ESPACO_BOTOES = 10;
    private static final int LARGURA_CONTEUDO = 340;
    private static final int ALTURA_BOTAO = 40;
    private static final float TAMANHO_TITULO = 22f;
    private static final float TAMANHO_TEXTO = 13f;

    private final JPanel conteudo = new JPanel();
    private final JPanel botoes = new JPanel(new GridLayout(1, 0, ESPACO_BOTOES, 0));
    private boolean confirmado;

    private Dialogo(Component pai, String titulo) {
        super(SwingUtilities.getWindowAncestor(pai), titulo, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(TRANSPARENTE);
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.add(new Rotulo(titulo, TAMANHO_TITULO));
        botoes.setOpaque(false);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoes.setPreferredSize(new Dimension(LARGURA_CONTEUDO, ALTURA_BOTAO));
        botoes.setMaximumSize(new Dimension(LARGURA_CONTEUDO, ALTURA_BOTAO));
        getRootPane().registerKeyboardAction(evento -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Pede a confirmação de uma ação destrutiva. O foco começa em "Cancelar".
     *
     * @param pai componente sobre o qual a janela aparece
     * @param textos título, explicação e texto do botão que confirma
     * @return verdadeiro se o usuário confirmou
     */
    public static boolean confirmar(Component pai, Confirmacao textos) {
        Dialogo dialogo = new Dialogo(pai, textos.titulo());
        dialogo.adicionarTexto(textos.explicacao());
        dialogo.adicionarBotao("Cancelar", Tema.FUNDO_BOTAO_SECUNDARIO, dialogo::dispose);
        dialogo.adicionarBotao(textos.botao(), Tema.PERIGO, () -> {
            dialogo.confirmado = true;
            dialogo.dispose();
        });
        dialogo.exibir(pai);
        return dialogo.confirmado;
    }

    /**
     * Exibe uma mensagem curta, com o botão "Ok".
     *
     * @param pai componente sobre o qual a janela aparece
     * @param titulo mensagem exibida
     */
    public static void informar(Component pai, String titulo) {
        Dialogo dialogo = new Dialogo(pai, titulo);
        dialogo.adicionarBotao("Ok", Tema.LILAS, dialogo::dispose)
                .setForeground(Tema.TEXTO_ESCURO);
        dialogo.exibir(pai);
    }

    private void adicionarTexto(String texto) {
        // Área de texto em vez de rótulo HTML: quebra linhas sem interpretar o nome do jogo.
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(Tema.TEXTO_SECUNDARIO);
        area.setFont(Tema.fonte(Font.PLAIN, TAMANHO_TEXTO));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        // A largura precisa vir antes, para a altura considerar a quebra de linhas.
        area.setSize(LARGURA_CONTEUDO, Short.MAX_VALUE);
        area.setPreferredSize(new Dimension(LARGURA_CONTEUDO, area.getPreferredSize().height));
        conteudo.add(Box.createVerticalStrut(ESPACO));
        conteudo.add(area);
    }

    private BotaoArredondado adicionarBotao(String texto, Color cor, Runnable acao) {
        BotaoArredondado botao = new BotaoArredondado(texto, cor);
        botao.addActionListener(evento -> acao.run());
        botoes.add(botao);
        return botao;
    }

    private void exibir(Component pai) {
        conteudo.add(Box.createVerticalStrut(ESPACO));
        conteudo.add(botoes);
        PainelArredondado cartao = new PainelArredondado(Tema.FUNDO_CARTAO_CONTA, Tema.BORDA);
        cartao.setBorder(BorderFactory.createEmptyBorder(MARGEM, MARGEM, MARGEM, MARGEM));
        cartao.add(conteudo, BorderLayout.CENTER);
        setContentPane(cartao);
        getRootPane().setDefaultButton((BotaoArredondado) botoes.getComponent(0));
        pack();
        setLocationRelativeTo(pai);
        setVisible(true);
    }

    /**
     * Textos de um pedido de confirmação.
     *
     * @param titulo pergunta
     * @param explicacao detalhes da ação
     * @param botao texto do botão que confirma
     */
    public record Confirmacao(String titulo, String explicacao, String botao) {
    }
}
