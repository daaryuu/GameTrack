package gametrack.front.view;

import gametrack.front.cliente.JogoCliente;
import gametrack.front.cliente.NaoAutenticadoException;
import gametrack.front.componentes.AcaoJogo;
import gametrack.front.componentes.BotaoArredondado;
import gametrack.front.componentes.BotaoLink;
import gametrack.front.componentes.CaixaOpcoes;
import gametrack.front.componentes.CampoTextoComDica;
import gametrack.front.componentes.Dialogo;
import gametrack.front.componentes.ModeloTabelaJogos;
import gametrack.front.componentes.PainelArredondado;
import gametrack.front.componentes.TabelaJogos;
import gametrack.front.componentes.Tema;
import gametrack.front.dto.FiltroJogoDTO;
import gametrack.front.dto.JogoDTO;
import gametrack.front.dto.Perfil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Tela I_CatalogoLista: lista os jogos do catálogo e permite filtrar por título, gênero,
 * desenvolvedora e ano (RF004), no layout do protótipo do Figma. O administrador também exclui
 * jogos por ela, depois de confirmar (RF006).
 * A tela só mostra dados; o acesso a eles é feito pelo {@link JogoCliente}, em segundo plano,
 * para a janela não travar.
 */
public class CatalogoListaView extends JPanel {

    private static final String TITULO_TELA = "Catálogo de jogos";
    private static final String SUBTITULO_TELA =
            "Encontre seu próximo jogo. Explore por título, gênero ou estúdio.";
    private static final String MENSAGEM_LISTA_VAZIA = "Nenhum jogo encontrado.";
    private static final String JOGO_EXCLUIDO = "Jogo excluído com sucesso!";
    private static final String TITULO_ERRO = "Erro";
    private static final String PADRAO_GENERO = "Todos os gêneros";
    private static final String PADRAO_DESENVOLVEDORA = "Todas";
    private static final String PADRAO_ANO = "Todos";
    private static final List<AcaoJogo> ACOES_USUARIO = List.of(AcaoJogo.VER_DETALHES);
    private static final List<AcaoJogo> ACOES_ADMINISTRADOR =
            List.of(AcaoJogo.VER_DETALHES, AcaoJogo.ALTERAR, AcaoJogo.EXCLUIR);
    private static final int ESPACO_BOTOES = 10;
    private static final int MARGEM = 24;
    private static final int ESPACO = 16;
    private static final int ESPACO_PEQUENO = 6;
    private static final int ALTURA_CAMPO = 35;
    private static final int LARGURA_TITULO = 175;
    private static final int LARGURA_GENERO = 185;
    private static final int LARGURA_DESENVOLVEDORA = 165;
    private static final int LARGURA_ANO = 100;
    private static final float TAMANHO_TITULO = 26f;
    private static final float TAMANHO_TEXTO = 13f;
    private static final float TAMANHO_LEGENDA = 12f;

    private final JogoCliente cliente;
    private final ModeloTabelaJogos modeloTabela = new ModeloTabelaJogos();
    private final CampoTextoComDica campoTitulo = new CampoTextoComDica("Buscar por título");
    private final CaixaOpcoes campoGenero = new CaixaOpcoes();
    private final CaixaOpcoes campoDesenvolvedora = new CaixaOpcoes();
    private final CaixaOpcoes campoAno = new CaixaOpcoes();
    private final JLabel contagem = criarRotulo(" ", Font.BOLD, TAMANHO_TEXTO, Tema.TEXTO);
    private final JLabel mensagem =
            criarRotulo(" ", Font.PLAIN, TAMANHO_TEXTO, Tema.SUCESSO);
    private final JLabel rodapeEsquerdo =
            criarRotulo(" ", Font.PLAIN, TAMANHO_LEGENDA, Tema.TEXTO_SECUNDARIO);
    private final JLabel rodapeDireito =
            criarRotulo(" ", Font.PLAIN, TAMANHO_LEGENDA, Tema.TEXTO_SECUNDARIO);
    private final BotaoArredondado botaoBiblioteca =
            new BotaoArredondado("Minha biblioteca", Tema.FUNDO_BOTAO_SECUNDARIO);
    private final BotaoArredondado botaoAdicionar =
            new BotaoArredondado("Adicionar Jogo", Tema.ROXO);
    private final boolean administrador;
    private final TabelaJogos tabela;
    private Runnable aoExpirarSessao = () -> { };
    private int totalCatalogo;

    /**
     * Cria a tela e já carrega todos os jogos do catálogo.
     *
     * @param cliente acesso ao catálogo, no servidor ou em memória
     * @param perfil perfil da conta; só o administrador vê as ações de manter o catálogo
     */
    public CatalogoListaView(JogoCliente cliente, Perfil perfil) {
        this.cliente = cliente;
        this.administrador = perfil == Perfil.ADMINISTRADOR;
        this.tabela = new TabelaJogos(
                modeloTabela, administrador ? ACOES_ADMINISTRADOR : ACOES_USUARIO);
        setBackground(Tema.FUNDO_JANELA);
        setLayout(new BorderLayout(0, ESPACO));
        setBorder(BorderFactory.createEmptyBorder(MARGEM, MARGEM, MARGEM, MARGEM));
        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpo(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
        definirOpcoesPadrao();
        tabela.definirAoAcionar(AcaoJogo.EXCLUIR, this::confirmarExclusao);
        carregarJogos(FiltroJogoDTO.semFiltro());
    }

    /**
     * Define o que acontece ao clicar em "Minha biblioteca".
     *
     * @param acao ação a executar
     */
    public void definirAoAbrirBiblioteca(Runnable acao) {
        botaoBiblioteca.addActionListener(evento -> acao.run());
    }

    /**
     * Define o que acontece quando o servidor não reconhece mais a sessão (RNF007).
     *
     * @param acao ação a executar, como voltar ao login
     */
    public void definirAoExpirarSessao(Runnable acao) {
        this.aoExpirarSessao = acao;
    }

    /**
     * Define o que acontece ao clicar em "Ver detalhes" de um jogo.
     *
     * @param acao recebe o jogo da linha clicada
     */
    public void definirAoVerDetalhes(Consumer<JogoDTO> acao) {
        tabela.definirAoAcionar(AcaoJogo.VER_DETALHES, acao);
    }

    /**
     * Define o que acontece quando o administrador clica em "Adicionar Jogo" (RF003).
     *
     * @param acao ação a executar
     */
    public void definirAoAdicionar(Runnable acao) {
        botaoAdicionar.addActionListener(evento -> acao.run());
    }

    /**
     * Define o que acontece quando o administrador clica em "Alterar jogo" (RF005).
     *
     * @param acao recebe o jogo da linha clicada
     */
    public void definirAoAlterar(Consumer<JogoDTO> acao) {
        tabela.definirAoAcionar(AcaoJogo.ALTERAR, acao);
    }

    /**
     * Recarrega o catálogo, sem filtros, e exibe uma mensagem de sucesso até a próxima busca.
     *
     * @param texto mensagem exibida, como a de jogo cadastrado
     */
    public void recarregar(String texto) {
        limparFiltros();
        mensagem.setText(texto);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = criarPainelTransparente(new BorderLayout());
        JPanel botoes = criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botoes.add(botaoBiblioteca);
        if (administrador) {
            botoes.add(Box.createHorizontalStrut(ESPACO_BOTOES));
            botoes.add(botaoAdicionar);
        }
        cabecalho.add(criarTextosDoCabecalho(), BorderLayout.WEST);
        cabecalho.add(botoes, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarTextosDoCabecalho() {
        JPanel textos = criarPainelTransparente(null);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(criarRotulo(TITULO_TELA, Font.BOLD, TAMANHO_TITULO, Tema.TEXTO));
        textos.add(Box.createVerticalStrut(ESPACO_PEQUENO));
        textos.add(criarRotulo(SUBTITULO_TELA, Font.PLAIN, TAMANHO_TEXTO,
                Tema.TEXTO_SECUNDARIO));
        return textos;
    }

    private JPanel criarCorpo() {
        JPanel corpo = criarPainelTransparente(new BorderLayout(0, ESPACO));
        JPanel topo = criarPainelTransparente(new BorderLayout(0, ESPACO));
        topo.add(criarCartaoFiltros(), BorderLayout.CENTER);
        JPanel situacao = criarPainelTransparente(new BorderLayout());
        situacao.add(contagem, BorderLayout.WEST);
        situacao.add(mensagem, BorderLayout.EAST);
        topo.add(situacao, BorderLayout.SOUTH);
        corpo.add(topo, BorderLayout.NORTH);
        corpo.add(criarCartaoTabela(), BorderLayout.CENTER);
        return corpo;
    }

    private PainelArredondado criarCartaoFiltros() {
        PainelArredondado cartao = new PainelArredondado(Tema.FUNDO_CARTAO, Tema.BORDA);
        cartao.setBorder(BorderFactory.createEmptyBorder(ESPACO, ESPACO, ESPACO, ESPACO));
        cartao.add(criarTituloDosFiltros(), BorderLayout.NORTH);
        cartao.add(criarGradeDeFiltros(), BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarTituloDosFiltros() {
        JPanel linha = criarPainelTransparente(new BorderLayout());
        BotaoLink limpar = new BotaoLink("Limpar filtros");
        limpar.addActionListener(evento -> limparFiltros());
        linha.setBorder(BorderFactory.createEmptyBorder(0, 0, ESPACO, 0));
        linha.add(criarRotulo("Filtrar jogos", Font.BOLD, TAMANHO_TEXTO, Tema.TEXTO),
                BorderLayout.WEST);
        linha.add(limpar, BorderLayout.EAST);
        return linha;
    }

    private JPanel criarGradeDeFiltros() {
        JPanel grade = criarPainelTransparente(new GridBagLayout());
        campoTitulo.addActionListener(evento -> aplicarFiltros());
        adicionarColuna(grade, 0, new ColunaFiltro("Título", campoTitulo, LARGURA_TITULO, 3.2));
        adicionarColuna(grade, 1, new ColunaFiltro("Gênero", campoGenero, LARGURA_GENERO, 2.4));
        adicionarColuna(grade, 2, new ColunaFiltro("Desenvolvedora", campoDesenvolvedora,
                LARGURA_DESENVOLVEDORA, 2.8));
        adicionarColuna(grade, 3, new ColunaFiltro("Ano", campoAno, LARGURA_ANO, 1.6));
        adicionarBotaoFiltrar(grade);
        return grade;
    }

    private void adicionarColuna(JPanel grade, int posicao, ColunaFiltro coluna) {
        GridBagConstraints rotulo = restricao(posicao, 0, coluna.peso());
        rotulo.insets = new Insets(0, 0, ESPACO_PEQUENO, ESPACO);
        grade.add(criarRotulo(coluna.rotulo(), Font.PLAIN, TAMANHO_LEGENDA,
                Tema.TEXTO_SECUNDARIO), rotulo);
        coluna.campo().setPreferredSize(new Dimension(coluna.largura(), ALTURA_CAMPO));
        GridBagConstraints campo = restricao(posicao, 1, coluna.peso());
        campo.insets = new Insets(0, 0, 0, ESPACO);
        grade.add(coluna.campo(), campo);
    }

    private void adicionarBotaoFiltrar(JPanel grade) {
        BotaoArredondado botao = new BotaoArredondado("Filtrar", Tema.ROXO);
        botao.addActionListener(evento -> aplicarFiltros());
        GridBagConstraints restricao = restricao(4, 1, 0);
        restricao.fill = GridBagConstraints.VERTICAL;
        grade.add(botao, restricao);
    }

    private GridBagConstraints restricao(int coluna, int linha, double peso) {
        GridBagConstraints restricao = new GridBagConstraints();
        restricao.gridx = coluna;
        restricao.gridy = linha;
        restricao.weightx = peso;
        restricao.anchor = GridBagConstraints.WEST;
        restricao.fill = GridBagConstraints.HORIZONTAL;
        return restricao;
    }

    private PainelArredondado criarCartaoTabela() {
        PainelArredondado cartao = new PainelArredondado(Tema.LINHA_ESCURA, Tema.BORDA);
        cartao.add(tabela.criarPainelRolagem(), BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarRodape() {
        JPanel rodape = criarPainelTransparente(new BorderLayout());
        rodape.add(rodapeEsquerdo, BorderLayout.WEST);
        rodape.add(rodapeDireito, BorderLayout.EAST);
        return rodape;
    }

    private void definirOpcoesPadrao() {
        campoGenero.definirOpcoes(PADRAO_GENERO, List.of());
        campoDesenvolvedora.definirOpcoes(PADRAO_DESENVOLVEDORA, List.of());
        campoAno.definirOpcoes(PADRAO_ANO, List.of());
    }

    private void aplicarFiltros() {
        carregarJogos(montarFiltro());
    }

    private void limparFiltros() {
        campoTitulo.setText("");
        campoGenero.setSelectedIndex(0);
        campoDesenvolvedora.setSelectedIndex(0);
        campoAno.setSelectedIndex(0);
        carregarJogos(FiltroJogoDTO.semFiltro());
    }

    private FiltroJogoDTO montarFiltro() {
        String titulo = campoTitulo.getText().trim();
        return new FiltroJogoDTO(titulo.isEmpty() ? null : titulo,
                opcaoEscolhida(campoGenero),
                opcaoEscolhida(campoDesenvolvedora),
                opcaoEscolhida(campoAno));
    }

    private String opcaoEscolhida(CaixaOpcoes caixa) {
        return caixa.getSelectedIndex() <= 0 ? null : (String) caixa.getSelectedItem();
    }

    private void carregarJogos(FiltroJogoDTO filtro) {
        mensagem.setText(" ");
        new Tarefa<>(() -> cliente.listar(filtro),
                jogos -> exibirJogos(jogos, filtro), this::tratarErro).execute();
    }

    private void confirmarExclusao(JogoDTO jogo) {
        boolean confirmado = Dialogo.confirmar(this, new Dialogo.Confirmacao(
                "Excluir jogo do catálogo?",
                "Deseja excluir “" + jogo.titulo() + "” do catálogo?\n"
                        + "Esta ação não pode ser desfeita.",
                "Excluir jogo"));
        if (confirmado) {
            excluir(jogo);
        }
    }

    private void excluir(JogoDTO jogo) {
        new Tarefa<>(() -> {
            cliente.excluir(jogo.id());
            return jogo;
        }, excluido -> concluirExclusao(), this::tratarErro).execute();
    }

    private void concluirExclusao() {
        limparFiltros();
        Dialogo.informar(this, JOGO_EXCLUIDO);
    }

    private void exibirJogos(List<JogoDTO> jogos, FiltroJogoDTO filtro) {
        modeloTabela.definirJogos(jogos);
        if (FiltroJogoDTO.semFiltro().equals(filtro)) {
            atualizarCatalogoCompleto(jogos);
        }
        atualizarTextos(jogos.size());
    }

    private void atualizarCatalogoCompleto(List<JogoDTO> jogos) {
        totalCatalogo = jogos.size();
        campoGenero.definirOpcoes(PADRAO_GENERO, valoresUnicos(jogos, JogoDTO::genero));
        campoDesenvolvedora.definirOpcoes(PADRAO_DESENVOLVEDORA,
                valoresUnicos(jogos, JogoDTO::desenvolvedora));
        campoAno.definirOpcoes(PADRAO_ANO,
                valoresUnicos(jogos, jogo -> String.valueOf(jogo.ano())));
    }

    private List<String> valoresUnicos(List<JogoDTO> jogos, Function<JogoDTO, String> campo) {
        return jogos.stream().map(campo).distinct().sorted().toList();
    }

    private void atualizarTextos(int exibidos) {
        contagem.setText(exibidos == 0 ? MENSAGEM_LISTA_VAZIA : textoContagem(exibidos));
        rodapeEsquerdo.setText(exibidos == totalCatalogo
                ? "Exibindo todos os " + totalCatalogo + " jogos do catálogo"
                : "Exibindo " + exibidos + " dos " + totalCatalogo + " jogos do catálogo");
        rodapeDireito.setText(exibidos + " de " + totalCatalogo);
    }

    private String textoContagem(int exibidos) {
        return exibidos == 1 ? "1 jogo encontrado" : exibidos + " jogos encontrados";
    }

    private void tratarErro(Throwable causa) {
        if (causa instanceof NaoAutenticadoException) {
            aoExpirarSessao.run();
        } else {
            exibirErro(causa.getMessage());
        }
    }

    private void exibirErro(String texto) {
        JOptionPane.showMessageDialog(this, texto, TITULO_ERRO, JOptionPane.ERROR_MESSAGE);
    }

    private static JLabel criarRotulo(String texto, int estilo, float tamanho, Color cor) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Tema.fonte(estilo, tamanho));
        rotulo.setForeground(cor);
        return rotulo;
    }

    private static JPanel criarPainelTransparente(java.awt.LayoutManager layout) {
        JPanel painel = new JPanel(layout);
        painel.setOpaque(false);
        return painel;
    }

    /** Uma coluna da área de filtros: rótulo, campo, largura mínima e peso na largura. */
    private record ColunaFiltro(String rotulo, JComponent campo, int largura, double peso) {
    }
}
