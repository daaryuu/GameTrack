package gametrack.front.componentes;

import gametrack.front.dto.JogoDTO;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de tabela que exibe jogos do catálogo, com as colunas título, gênero,
 * desenvolvedora, ano e a ação "Ver detalhes". Pode ser usado por qualquer tela que
 * liste jogos.
 */
public class ModeloTabelaJogos extends AbstractTableModel {

    /** Índice da coluna do título. */
    public static final int COLUNA_TITULO = 0;
    private static final int COLUNA_GENERO = 1;
    private static final int COLUNA_DESENVOLVEDORA = 2;
    private static final int COLUNA_ANO = 3;
    private static final String TEXTO_ACAO = "Ver detalhes";
    private static final String[] NOMES_COLUNAS =
            {"Título", "Gênero", "Desenvolvedora", "Ano", "Ação"};

    /** Índice da coluna com o link "Ver detalhes". */
    public static final int COLUNA_ACAO = 4;

    private List<JogoDTO> jogos = List.of();

    /**
     * Substitui os jogos exibidos e atualiza a tabela.
     *
     * @param novosJogos jogos a exibir
     */
    public void definirJogos(List<JogoDTO> novosJogos) {
        this.jogos = List.copyOf(novosJogos);
        fireTableDataChanged();
    }

    /**
     * Devolve o jogo exibido em uma linha, para as telas que atuam sobre a seleção.
     *
     * @param linha índice da linha, a partir de 0
     * @return o jogo daquela linha
     */
    public JogoDTO jogoNaLinha(int linha) {
        return jogos.get(linha);
    }

    @Override
    public int getRowCount() {
        return jogos.size();
    }

    @Override
    public int getColumnCount() {
        return NOMES_COLUNAS.length;
    }

    @Override
    public String getColumnName(int coluna) {
        return NOMES_COLUNAS[coluna];
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        JogoDTO jogo = jogos.get(linha);
        return switch (coluna) {
            case COLUNA_TITULO -> jogo.titulo();
            case COLUNA_GENERO -> jogo.genero();
            case COLUNA_DESENVOLVEDORA -> jogo.desenvolvedora();
            case COLUNA_ANO -> jogo.ano();
            case COLUNA_ACAO -> TEXTO_ACAO;
            default -> throw new IllegalArgumentException("Coluna inexistente: " + coluna);
        };
    }
}
