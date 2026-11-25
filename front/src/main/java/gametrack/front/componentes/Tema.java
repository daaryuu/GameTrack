package gametrack.front.componentes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Cores, fontes e utilitários de desenho do tema escuro do GameTrack, conforme o protótipo
 * do Figma. Todos os componentes da interface devem buscar as cores aqui.
 */
public final class Tema {

    /** Fundo da janela. */
    public static final Color FUNDO_JANELA = new Color(0x10121C);
    /** Fundo dos cartões (filtros e tabela). */
    public static final Color FUNDO_CARTAO = new Color(0x1A1D2B);
    /** Fundo dos campos de texto e das caixas de opções. */
    public static final Color FUNDO_CAMPO = new Color(0x12141F);
    /** Fundo do botão secundário, como "Minha biblioteca". */
    public static final Color FUNDO_BOTAO_SECUNDARIO = new Color(0x252839);
    /** Linhas de contorno dos cartões e campos. */
    public static final Color BORDA = new Color(0x2B2F44);
    /** Texto principal. */
    public static final Color TEXTO = new Color(0xF2F3F7);
    /** Texto das células da tabela, um pouco mais suave que o principal. */
    public static final Color TEXTO_CELULA = new Color(0xC9CCDA);
    /** Texto de apoio (rótulos, legendas e cabeçalho da tabela). */
    public static final Color TEXTO_SECUNDARIO = new Color(0x9CA1B5);
    /** Cor de destaque dos botões principais. */
    public static final Color ROXO = new Color(0x6C3FC8);
    /** Fundo do cartão das telas de conta. */
    public static final Color FUNDO_CARTAO_CONTA = new Color(0x151722);
    /** Fundo do botão principal das telas de conta. */
    public static final Color LILAS = new Color(0xB69AF8);
    /** Texto escuro, para uso sobre o lilás. */
    public static final Color TEXTO_ESCURO = new Color(0x14161F);
    /** Mensagens de erro. */
    public static final Color ERRO = new Color(0xFF8A8A);
    /** Mensagens de sucesso. */
    public static final Color SUCESSO = new Color(0x7EE2A8);
    /** Cor dos links. */
    public static final Color LINK = new Color(0xB79CFF);
    /** Fundo do cabeçalho da tabela. */
    public static final Color CABECALHO_TABELA = new Color(0x222639);
    /** Linha par da tabela. */
    public static final Color LINHA_ESCURA = new Color(0x171A28);
    /** Linha ímpar da tabela. */
    public static final Color LINHA_CLARA = new Color(0x1D2030);
    /** Linha selecionada da tabela. */
    public static final Color SELECAO = new Color(0x2C3150);
    /** Raio dos cantos dos cartões. */
    public static final int RAIO = 12;

    private static final String FAMILIA_FONTE = "SansSerif";
    private static final int INTENSIDADE_CLAREAR = 18;
    private static final int COMPONENTE_MAXIMO = 255;

    private Tema() {
    }

    /**
     * Cria uma fonte do tema.
     *
     * @param estilo  estilo da fonte (por exemplo, {@link Font#BOLD})
     * @param tamanho tamanho em pontos
     * @return a fonte pedida
     */
    public static Font fonte(int estilo, float tamanho) {
        return new Font(FAMILIA_FONTE, estilo, 1).deriveFont(tamanho);
    }

    /**
     * Clareia uma cor levemente, para o efeito de passar o mouse.
     *
     * @param cor cor original
     * @return cor um pouco mais clara
     */
    public static Color clarear(Color cor) {
        return new Color(
                Math.min(COMPONENTE_MAXIMO, cor.getRed() + INTENSIDADE_CLAREAR),
                Math.min(COMPONENTE_MAXIMO, cor.getGreen() + INTENSIDADE_CLAREAR),
                Math.min(COMPONENTE_MAXIMO, cor.getBlue() + INTENSIDADE_CLAREAR));
    }

    /**
     * Cria uma cópia do contexto gráfico com bordas e textos suavizados.
     * Quem chama deve liberar a cópia com {@code dispose()}.
     *
     * @param grafico contexto gráfico original
     * @return cópia com suavização ligada
     */
    public static Graphics2D suavizar(Graphics grafico) {
        Graphics2D copia = (Graphics2D) grafico.create();
        copia.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        copia.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return copia;
    }
}
