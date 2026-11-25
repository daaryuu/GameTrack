package gametrack.front.componentes;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Desenha as ações de uma linha da {@link TabelaJogos}, alinhadas à direita, e informa qual
 * delas está em um ponto da célula.
 */
class CelulaAcoes extends JComponent implements TableCellRenderer {

    private static final int MARGEM = 16;
    private static final int ESPACO = 10;
    private static final int PREENCHIMENTO = 11;
    private static final int PREENCHIMENTO_CURTO = 9;
    private static final int ALTURA_BOTAO = 28;
    private static final int ARCO = 8;
    private static final float TAMANHO_FONTE = 13f;
    private static final float TAMANHO_FONTE_BOTAO = 12f;

    private final List<AcaoJogo> acoes;

    CelulaAcoes(List<AcaoJogo> acoes) {
        this.acoes = List.copyOf(acoes);
        setOpaque(true);
    }

    int larguraNecessaria() {
        int larguraTotal = acoes.stream().mapToInt(this::largura).sum();
        return larguraTotal + ESPACO * (acoes.size() - 1) + MARGEM * 2;
    }

    Optional<AcaoJogo> acaoNoPonto(Point ponto, Dimension celula) {
        return areas(celula).entrySet().stream()
                .filter(area -> area.getValue().contains(ponto))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    Map<AcaoJogo, Rectangle> areas(Dimension celula) {
        Map<AcaoJogo, Rectangle> areas = new EnumMap<>(AcaoJogo.class);
        int direita = celula.width - MARGEM;
        int topo = (celula.height - ALTURA_BOTAO) / 2;
        for (AcaoJogo acao : acoes.reversed()) {
            int largura = largura(acao);
            areas.put(acao, new Rectangle(direita - largura, topo, largura, ALTURA_BOTAO));
            direita -= largura + ESPACO;
        }
        return areas;
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabela, Object valor,
            boolean selecionada, boolean comFoco, int linha, int coluna) {
        setBackground(TabelaJogos.corDaLinha(selecionada, linha));
        return this;
    }

    @Override
    protected void paintComponent(Graphics grafico) {
        Graphics2D g2 = Tema.suavizar(grafico);
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        areas(getSize()).forEach((acao, area) -> desenhar(g2, acao, area));
        g2.dispose();
    }

    private void desenhar(Graphics2D g2, AcaoJogo acao, Rectangle area) {
        if (acao.eBotao()) {
            desenharFundo(g2, acao, area);
        }
        FontMetrics medidas = g2.getFontMetrics(fonte(acao));
        int x = area.x + (area.width - medidas.stringWidth(acao.texto())) / 2;
        int base = area.y + (area.height + medidas.getAscent() - medidas.getDescent()) / 2;
        g2.setFont(fonte(acao));
        g2.setColor(acao.corTexto());
        g2.drawString(acao.texto(), x, base);
    }

    private void desenharFundo(Graphics2D g2, AcaoJogo acao, Rectangle area) {
        RoundRectangle2D contorno = new RoundRectangle2D.Float(area.x + 0.5f, area.y + 0.5f,
                area.width - 1, area.height - 1, ARCO, ARCO);
        g2.setColor(acao.fundo());
        g2.fill(contorno);
        g2.setColor(acao.borda());
        g2.draw(contorno);
    }

    private int largura(AcaoJogo acao) {
        int larguraTexto = getFontMetrics(fonte(acao)).stringWidth(acao.texto());
        if (!acao.eBotao()) {
            return larguraTexto;
        }
        boolean curto = acao.texto().length() == 1;
        return larguraTexto + 2 * (curto ? PREENCHIMENTO_CURTO : PREENCHIMENTO);
    }

    private Font fonte(AcaoJogo acao) {
        return acao.eBotao()
                ? Tema.fonte(Font.BOLD, TAMANHO_FONTE_BOTAO)
                : Tema.fonte(Font.PLAIN, TAMANHO_FONTE);
    }
}
