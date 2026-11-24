package gametrack.front.componentes;

import gametrack.front.dto.JogoDTO;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Tabela de jogos no tema escuro: linhas alternadas, cabeçalho discreto e a coluna
 * "Ver detalhes" como link clicável.
 */
public class TabelaJogos extends JTable {

    private static final int ALTURA_LINHA = 47;
    private static final int ALTURA_CABECALHO = 36;
    private static final int PREENCHIMENTO = 16;
    private static final int LARGURA_BARRA = 10;
    private static final float TAMANHO_FONTE = 13f;
    private static final float TAMANHO_FONTE_CABECALHO = 12f;
    private static final int[] LARGURAS_COLUNAS = {300, 200, 250, 100, 150};

    private final ModeloTabelaJogos modelo;
    private Consumer<JogoDTO> aoVerDetalhes = jogo -> { };

    /**
     * Cria a tabela.
     *
     * @param modelo jogos exibidos
     */
    public TabelaJogos(ModeloTabelaJogos modelo) {
        super(modelo);
        this.modelo = modelo;
        configurarAparencia();
        configurarColunas();
        configurarCliqueNaAcao();
    }

    /**
     * Define o que acontece ao clicar em "Ver detalhes".
     *
     * @param acao recebe o jogo da linha clicada
     */
    public void definirAoVerDetalhes(Consumer<JogoDTO> acao) {
        this.aoVerDetalhes = acao;
    }

    /**
     * Cria o painel com barra de rolagem escura que contém a tabela.
     *
     * @return painel de rolagem pronto para ser adicionado à tela
     */
    public JScrollPane criarPainelRolagem() {
        JScrollPane painel = new JScrollPane(this);
        painel.setBorder(BorderFactory.createEmptyBorder());
        painel.getViewport().setBackground(Tema.LINHA_ESCURA);
        painel.getVerticalScrollBar().setUI(new BarraRolagemEscura());
        painel.getVerticalScrollBar().setPreferredSize(new Dimension(LARGURA_BARRA, 0));
        return painel;
    }

    private void configurarAparencia() {
        setRowHeight(ALTURA_LINHA);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);
        setBackground(Tema.LINHA_ESCURA);
        setForeground(Tema.TEXTO);
        setSelectionBackground(Tema.SELECAO);
        setSelectionForeground(Tema.TEXTO);
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        setDefaultRenderer(Object.class, new RenderizadorCelula());
        JTableHeader cabecalho = getTableHeader();
        cabecalho.setReorderingAllowed(false);
        cabecalho.setResizingAllowed(false);
        cabecalho.setDefaultRenderer(new RenderizadorCabecalho());
        cabecalho.setPreferredSize(new Dimension(0, ALTURA_CABECALHO));
    }

    private void configurarColunas() {
        for (int coluna = 0; coluna < LARGURAS_COLUNAS.length; coluna++) {
            getColumnModel().getColumn(coluna).setPreferredWidth(LARGURAS_COLUNAS[coluna]);
        }
    }

    private void configurarCliqueNaAcao() {
        MouseAdapter ouvinte = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                JogoDTO jogo = jogoNaAcao(evento.getPoint());
                if (jogo != null) {
                    aoVerDetalhes.accept(jogo);
                }
            }

            @Override
            public void mouseMoved(MouseEvent evento) {
                boolean sobreAcao = jogoNaAcao(evento.getPoint()) != null;
                setCursor(Cursor.getPredefinedCursor(
                        sobreAcao ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        };
        addMouseListener(ouvinte);
        addMouseMotionListener(ouvinte);
    }

    private JogoDTO jogoNaAcao(Point ponto) {
        int linha = rowAtPoint(ponto);
        boolean naAcao = columnAtPoint(ponto) == ModeloTabelaJogos.COLUNA_ACAO;
        return linha >= 0 && naAcao ? modelo.jogoNaLinha(linha) : null;
    }

    /** Desenho das células: linhas alternadas e a coluna de ação como link. */
    private static class RenderizadorCelula extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean comFoco, int linha, int coluna) {
            JLabel rotulo = (JLabel) super.getTableCellRendererComponent(tabela, valor,
                    selecionada, false, linha, coluna);
            boolean acao = coluna == ModeloTabelaJogos.COLUNA_ACAO;
            rotulo.setBorder(BorderFactory.createEmptyBorder(0, PREENCHIMENTO, 0, PREENCHIMENTO));
            rotulo.setFont(Tema.fonte(Font.PLAIN, TAMANHO_FONTE));
            rotulo.setBackground(corDaLinha(selecionada, linha));
            rotulo.setForeground(corDoTexto(coluna));
            rotulo.setHorizontalAlignment(acao ? SwingConstants.RIGHT : SwingConstants.LEFT);
            return rotulo;
        }

        private java.awt.Color corDaLinha(boolean selecionada, int linha) {
            if (selecionada) {
                return Tema.SELECAO;
            }
            return linha % 2 == 0 ? Tema.LINHA_ESCURA : Tema.LINHA_CLARA;
        }

        private java.awt.Color corDoTexto(int coluna) {
            if (coluna == ModeloTabelaJogos.COLUNA_ACAO) {
                return Tema.LINK;
            }
            return coluna == ModeloTabelaJogos.COLUNA_TITULO ? Tema.TEXTO : Tema.TEXTO_CELULA;
        }
    }

    /** Desenho do cabeçalho: texto discreto sobre fundo mais claro. */
    private static class RenderizadorCabecalho extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor,
                boolean selecionada, boolean comFoco, int linha, int coluna) {
            JLabel rotulo = (JLabel) super.getTableCellRendererComponent(tabela, valor,
                    false, false, linha, coluna);
            boolean acao = coluna == ModeloTabelaJogos.COLUNA_ACAO;
            rotulo.setOpaque(true);
            rotulo.setBackground(Tema.CABECALHO_TABELA);
            rotulo.setForeground(Tema.TEXTO_SECUNDARIO);
            rotulo.setFont(Tema.fonte(Font.PLAIN, TAMANHO_FONTE_CABECALHO));
            rotulo.setBorder(BorderFactory.createEmptyBorder(0, PREENCHIMENTO, 0, PREENCHIMENTO));
            rotulo.setHorizontalAlignment(acao ? SwingConstants.RIGHT : SwingConstants.LEFT);
            return rotulo;
        }
    }

    /** Barra de rolagem fina, sem botões, nas cores do tema. */
    private static class BarraRolagemEscura extends BasicScrollBarUI {

        private static final int ARCO = 8;

        @Override
        protected void configureScrollBarColors() {
            thumbColor = Tema.BORDA;
            trackColor = Tema.LINHA_ESCURA;
        }

        @Override
        protected JButton createDecreaseButton(int orientacao) {
            return botaoInvisivel();
        }

        @Override
        protected JButton createIncreaseButton(int orientacao) {
            return botaoInvisivel();
        }

        @Override
        protected void paintTrack(Graphics grafico, JComponent componente, Rectangle area) {
            grafico.setColor(trackColor);
            grafico.fillRect(area.x, area.y, area.width, area.height);
        }

        @Override
        protected void paintThumb(Graphics grafico, JComponent componente, Rectangle area) {
            Graphics2D g2 = Tema.suavizar(grafico);
            g2.setColor(thumbColor);
            g2.fill(new RoundRectangle2D.Float(area.x + 2, area.y, area.width - 4, area.height,
                    ARCO, ARCO));
            g2.dispose();
        }

        private JButton botaoInvisivel() {
            JButton botao = new JButton();
            botao.setPreferredSize(new Dimension(0, 0));
            return botao;
        }
    }
}
