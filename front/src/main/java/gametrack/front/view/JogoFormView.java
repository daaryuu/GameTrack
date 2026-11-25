package gametrack.front.view;

import gametrack.front.cliente.ErroServidorException;
import gametrack.front.cliente.JogoCliente;
import gametrack.front.cliente.NaoAutenticadoException;
import gametrack.front.componentes.BotaoArredondado;
import gametrack.front.componentes.BotaoLink;
import gametrack.front.componentes.CaixaOpcoes;
import gametrack.front.componentes.CampoTextoComDica;
import gametrack.front.componentes.PainelArredondado;
import gametrack.front.componentes.Rotulo;
import gametrack.front.componentes.Tema;
import gametrack.front.dto.JogoDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Formulário de cadastro e de alteração de jogo do catálogo (I_JogoForm), usado só pelo
 * administrador.
 *
 * @see RF003
 * @see RF005
 */
public class JogoFormView extends JPanel {

    private static final Textos CADASTRO = new Textos("Cadastrar jogo",
            "Adicione um novo jogo ao catálogo com as informações abaixo.",
            "O jogo será adicionado ao catálogo.", "Cadastrar jogo");
    private static final Textos ALTERACAO = new Textos("Alterar jogo",
            "Atualize as informações do jogo selecionado no catálogo.",
            "As alterações serão aplicadas ao catálogo.", "Salvar alterações");
    private static final String CARREGANDO = "Carregando os dados do jogo...";
    private static final String DADOS_NAO_INFORMADOS = "Informe todos os dados do jogo!";
    private static final String ANO_INVALIDO = "O ano de lançamento deve ser um número!";
    private static final String MENSAGEM_ERRO = "Erro inesperado!";
    private static final String PADRAO_GENERO = "Selecione o gênero";
    private static final List<String> GENEROS = List.of(
            "Ação", "Aventura", "Corrida", "Esporte", "Estratégia", "FPS", "Luta",
            "Metroidvania", "Plataforma", "Quebra-cabeça", "Roguelike", "RPG", "Simulação",
            "Sobrevivência", "Terror");

    private static final int MARGEM = 24;
    private static final int ESPACO = 16;
    private static final int ESPACO_PEQUENO = 8;
    private static final int ALTURA_CAMPO = 40;
    private static final int LARGURA_INICIAL = 100;
    private static final float TAMANHO_TITULO = 24f;
    private static final float TAMANHO_SECAO = 15f;

    private final JogoCliente cliente;
    private final CampoTextoComDica campoTitulo = new CampoTextoComDica("Digite o título do jogo");
    private final CaixaOpcoes campoGenero = new CaixaOpcoes();
    private final CampoTextoComDica campoAno = new CampoTextoComDica("Ex.: 2024");
    private final CampoTextoComDica campoDesenvolvedora =
            new CampoTextoComDica("Digite o nome da desenvolvedora");
    private final BotaoLink linkVoltar = new BotaoLink("Voltar ao catálogo");
    private final BotaoArredondado botaoCancelar =
            new BotaoArredondado("Cancelar", Tema.FUNDO_BOTAO_SECUNDARIO);
    private final Textos textos;
    private final Integer jogoId;
    private final BotaoArredondado botaoSalvar;
    private final Rotulo mensagem;
    private boolean prontoParaSalvar;
    private Runnable aoSalvar = () -> { };
    private Runnable aoExpirarSessao = () -> { };

    private JogoFormView(JogoCliente cliente, Textos textos, Integer jogoId) {
        super(new BorderLayout());
        this.cliente = cliente;
        this.textos = textos;
        this.jogoId = jogoId;
        this.prontoParaSalvar = jogoId == null;
        this.botaoSalvar = new BotaoArredondado(textos.botao(), Tema.ROXO);
        this.mensagem = new Rotulo(textos.dica(), Tema.TEXTO_SECUNDARIO);
        setBackground(Tema.FUNDO_JANELA);
        setBorder(BorderFactory.createEmptyBorder(MARGEM, MARGEM, MARGEM, MARGEM));
        campoGenero.definirOpcoes(PADRAO_GENERO, GENEROS);
        add(criarConteudo(), BorderLayout.NORTH);
        campoTitulo.addActionListener(evento -> salvar());
        campoAno.addActionListener(evento -> salvar());
        campoDesenvolvedora.addActionListener(evento -> salvar());
        botaoSalvar.addActionListener(evento -> salvar());
    }

    /**
     * Cria a tela vazia, para cadastrar um jogo.
     *
     * @param cliente acesso ao catálogo, no servidor ou em memória
     * @return tela de cadastro
     */
    public static JogoFormView paraCadastro(JogoCliente cliente) {
        return new JogoFormView(cliente, CADASTRO, null);
    }

    /**
     * Cria a tela para alterar um jogo e carrega os dados atuais dele.
     *
     * @param cliente acesso ao catálogo, no servidor ou em memória
     * @param jogoId identificador do jogo
     * @return tela de alteração
     */
    public static JogoFormView paraAlteracao(JogoCliente cliente, int jogoId) {
        JogoFormView formulario = new JogoFormView(cliente, ALTERACAO, jogoId);
        formulario.carregar();
        return formulario;
    }

    /**
     * Define o que fazer depois que o jogo for salvo.
     *
     * @param acao ação a executar, como voltar ao catálogo
     */
    public void definirAoSalvar(Runnable acao) {
        this.aoSalvar = acao;
    }

    /**
     * Define o que fazer quando o usuário desiste de salvar.
     *
     * @param acao ação a executar, como voltar ao catálogo
     */
    public void definirAoCancelar(Runnable acao) {
        linkVoltar.addActionListener(evento -> acao.run());
        botaoCancelar.addActionListener(evento -> acao.run());
    }

    /**
     * Define o que fazer quando o servidor não reconhece mais a sessão (RNF007).
     *
     * @param acao ação a executar, como voltar ao login
     */
    public void definirAoExpirarSessao(Runnable acao) {
        this.aoExpirarSessao = acao;
    }

    private JPanel criarConteudo() {
        JPanel conteudo = new JPanel(new BorderLayout(0, ESPACO));
        conteudo.setOpaque(false);
        conteudo.add(criarCabecalho(), BorderLayout.NORTH);
        conteudo.add(criarCartao(), BorderLayout.CENTER);
        conteudo.add(criarRodape(), BorderLayout.SOUTH);
        return conteudo;
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setOpaque(false);
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        linkVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Sem margem interna, para o link ficar alinhado ao título.
        linkVoltar.setBorder(BorderFactory.createEmptyBorder());
        cabecalho.add(linkVoltar);
        cabecalho.add(Box.createVerticalStrut(ESPACO_PEQUENO));
        cabecalho.add(new Rotulo(textos.titulo(), TAMANHO_TITULO));
        cabecalho.add(Box.createVerticalStrut(ESPACO_PEQUENO));
        cabecalho.add(new Rotulo(textos.subtitulo(), Tema.TEXTO_SECUNDARIO));
        return cabecalho;
    }

    private PainelArredondado criarCartao() {
        PainelArredondado cartao = new PainelArredondado(Tema.FUNDO_CARTAO, Tema.BORDA);
        cartao.setBorder(BorderFactory.createEmptyBorder(ESPACO, ESPACO, ESPACO, ESPACO));
        cartao.add(criarTituloDoCartao(), BorderLayout.NORTH);
        cartao.add(criarCampos(), BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarTituloDoCartao() {
        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.add(new Rotulo("Informações do jogo", TAMANHO_SECAO), BorderLayout.WEST);
        linha.add(new Rotulo("* Campos obrigatórios", Tema.TEXTO_SECUNDARIO), BorderLayout.EAST);
        return linha;
    }

    private JPanel criarCampos() {
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        campos.add(criarCampo("Título *", campoTitulo), posicao(0, 0, 2));
        GridBagConstraints genero = posicao(0, 1, 1);
        genero.insets.right = ESPACO;
        campos.add(criarCampo("Gênero *", campoGenero), genero);
        campos.add(criarCampo("Ano de lançamento *", campoAno), posicao(1, 1, 1));
        campos.add(criarCampo("Desenvolvedora *", campoDesenvolvedora), posicao(0, 2, 2));
        return campos;
    }

    private JPanel criarCampo(String texto, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, ESPACO_PEQUENO));
        painel.setOpaque(false);
        Rotulo rotulo = new Rotulo(texto, Tema.TEXTO_CELULA);
        rotulo.setLabelFor(campo);
        // Todos partem da mesma largura, para as colunas ficarem iguais.
        campo.setPreferredSize(new Dimension(LARGURA_INICIAL, ALTURA_CAMPO));
        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private GridBagConstraints posicao(int coluna, int linha, int colunasOcupadas) {
        GridBagConstraints posicao = new GridBagConstraints();
        posicao.gridx = coluna;
        posicao.gridy = linha;
        posicao.gridwidth = colunasOcupadas;
        posicao.weightx = 1;
        posicao.fill = GridBagConstraints.HORIZONTAL;
        posicao.insets = new Insets(ESPACO, 0, 0, 0);
        return posicao;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botoes.setOpaque(false);
        botoes.add(botaoCancelar);
        botoes.add(Box.createHorizontalStrut(ESPACO_PEQUENO));
        botoes.add(botaoSalvar);
        rodape.add(mensagem, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);
        return rodape;
    }

    private void carregar() {
        executar(CARREGANDO, () -> cliente.buscarPorId(jogoId), this::preencher);
    }

    private void preencher(JogoDTO jogo) {
        campoTitulo.setText(jogo.titulo());
        campoGenero.definirOpcoes(PADRAO_GENERO, generosCom(jogo.genero()));
        campoGenero.setSelectedItem(jogo.genero());
        campoAno.setText(String.valueOf(jogo.ano()));
        campoDesenvolvedora.setText(jogo.desenvolvedora());
        prontoParaSalvar = true;
        exibirMensagem(textos.dica(), Tema.TEXTO_SECUNDARIO);
    }

    private List<String> generosCom(String genero) {
        // O banco aceita qualquer gênero, e o do jogo não pode ser trocado sem o usuário pedir.
        return GENEROS.contains(genero) ? GENEROS
                : Stream.concat(GENEROS.stream(), Stream.of(genero)).toList();
    }

    private void salvar() {
        if (!botaoSalvar.isEnabled()) {
            return;
        }
        if (temCampoVazio()) {
            exibirMensagem(DADOS_NAO_INFORMADOS, Tema.ERRO);
            return;
        }
        try {
            enviar(montarJogo());
        } catch (NumberFormatException e) {
            exibirMensagem(ANO_INVALIDO, Tema.ERRO);
        }
    }

    private boolean temCampoVazio() {
        return campoTitulo.getText().isBlank() || campoGenero.getSelectedIndex() <= 0
                || campoAno.getText().isBlank() || campoDesenvolvedora.getText().isBlank();
    }

    private JogoDTO montarJogo() {
        return new JogoDTO(jogoId, campoTitulo.getText().trim(),
                (String) campoGenero.getSelectedItem(),
                campoDesenvolvedora.getText().trim(),
                Integer.parseInt(campoAno.getText().trim()));
    }

    private void enviar(JogoDTO jogo) {
        executar(textos.dica(), () -> {
            if (jogoId == null) {
                cliente.cadastrar(jogo);
            } else {
                cliente.alterar(jogo);
            }
            return jogo;
        }, salvo -> aoSalvar.run());
    }

    private <T> void executar(String texto, Supplier<T> tarefa, Consumer<T> aoConcluir) {
        exibirMensagem(texto, Tema.TEXTO_SECUNDARIO);
        habilitarAcoes(false);
        new Tarefa<>(tarefa, aoConcluir).execute();
    }

    private void habilitarAcoes(boolean habilitar) {
        botaoSalvar.setEnabled(habilitar && prontoParaSalvar);
        botaoCancelar.setEnabled(habilitar);
        linkVoltar.setEnabled(habilitar);
    }

    private void tratarErro(Throwable causa) {
        if (causa instanceof NaoAutenticadoException) {
            aoExpirarSessao.run();
        } else {
            exibirMensagem(causa instanceof ErroServidorException
                    ? causa.getMessage() : MENSAGEM_ERRO, Tema.ERRO);
        }
    }

    private void exibirMensagem(String texto, Color cor) {
        mensagem.setText(texto);
        mensagem.setForeground(cor);
    }

    /** Textos que mudam entre o cadastro e a alteração. */
    private record Textos(String titulo, String subtitulo, String dica, String botao) {
    }

    /** Executa uma operação fora da thread da interface, para a tela não travar. */
    private class Tarefa<T> extends SwingWorker<T, Void> {

        private final Supplier<T> operacao;
        private final Consumer<T> aoConcluir;

        Tarefa(Supplier<T> operacao, Consumer<T> aoConcluir) {
            this.operacao = operacao;
            this.aoConcluir = aoConcluir;
        }

        @Override
        protected T doInBackground() {
            return operacao.get();
        }

        @Override
        protected void done() {
            try {
                aoConcluir.accept(get());
            } catch (ExecutionException e) {
                tratarErro(e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                habilitarAcoes(true);
            }
        }
    }
}
