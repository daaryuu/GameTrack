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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Formulário de cadastro de jogo no catálogo (I_JogoForm), usado só pelo administrador.
 *
 * @see RF003
 */
public class JogoFormView extends JPanel {

    private static final String DICA_RODAPE = "O jogo será adicionado ao catálogo.";
    private static final String DADOS_NAO_INFORMADOS = "Informe todos os dados do jogo!";
    private static final String ANO_INVALIDO = "O ano de lançamento deve ser um número!";
    private static final String MENSAGEM_ERRO = "Erro inesperado ao cadastrar o jogo!";
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
    private final BotaoArredondado botaoCadastrar =
            new BotaoArredondado("Cadastrar jogo", Tema.ROXO);
    private final Rotulo mensagem = new Rotulo(DICA_RODAPE, Tema.TEXTO_SECUNDARIO);
    private Runnable aoCadastrar = () -> { };
    private Runnable aoExpirarSessao = () -> { };

    /**
     * Monta a tela.
     *
     * @param cliente acesso ao catálogo, no servidor ou em memória
     */
    public JogoFormView(JogoCliente cliente) {
        super(new BorderLayout());
        this.cliente = cliente;
        setBackground(Tema.FUNDO_JANELA);
        setBorder(BorderFactory.createEmptyBorder(MARGEM, MARGEM, MARGEM, MARGEM));
        campoGenero.definirOpcoes(PADRAO_GENERO, GENEROS);
        add(criarConteudo(), BorderLayout.NORTH);
        campoTitulo.addActionListener(evento -> cadastrar());
        campoAno.addActionListener(evento -> cadastrar());
        campoDesenvolvedora.addActionListener(evento -> cadastrar());
        botaoCadastrar.addActionListener(evento -> cadastrar());
    }

    /**
     * Define o que fazer depois que o jogo for cadastrado.
     *
     * @param acao ação a executar, como voltar ao catálogo
     */
    public void definirAoCadastrar(Runnable acao) {
        this.aoCadastrar = acao;
    }

    /**
     * Define o que fazer quando o usuário desiste do cadastro.
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
        cabecalho.add(new Rotulo("Cadastrar jogo", TAMANHO_TITULO));
        cabecalho.add(Box.createVerticalStrut(ESPACO_PEQUENO));
        cabecalho.add(new Rotulo("Adicione um novo jogo ao catálogo com as informações abaixo.",
                Tema.TEXTO_SECUNDARIO));
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
        botoes.add(botaoCadastrar);
        rodape.add(mensagem, BorderLayout.WEST);
        rodape.add(botoes, BorderLayout.EAST);
        return rodape;
    }

    private void cadastrar() {
        if (!botaoCadastrar.isEnabled()) {
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
        return new JogoDTO(null, campoTitulo.getText().trim(),
                (String) campoGenero.getSelectedItem(),
                campoDesenvolvedora.getText().trim(),
                Integer.parseInt(campoAno.getText().trim()));
    }

    private void enviar(JogoDTO jogo) {
        exibirMensagem(DICA_RODAPE, Tema.TEXTO_SECUNDARIO);
        habilitarAcoes(false);
        new EnvioJogo(jogo).execute();
    }

    private void habilitarAcoes(boolean habilitar) {
        botaoCadastrar.setEnabled(habilitar);
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

    /** Envia o jogo fora da thread da interface, para a tela não travar. */
    private class EnvioJogo extends SwingWorker<Void, Void> {

        private final JogoDTO jogo;

        EnvioJogo(JogoDTO jogo) {
            this.jogo = jogo;
        }

        @Override
        protected Void doInBackground() {
            cliente.cadastrar(jogo);
            return null;
        }

        @Override
        protected void done() {
            habilitarAcoes(true);
            try {
                get();
                aoCadastrar.run();
            } catch (ExecutionException e) {
                tratarErro(e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
