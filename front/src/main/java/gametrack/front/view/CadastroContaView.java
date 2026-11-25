package gametrack.front.view;

import gametrack.front.cliente.ErroServidorException;
import gametrack.front.cliente.UsuarioCliente;
import gametrack.front.componentes.BotaoArredondado;
import gametrack.front.componentes.CampoSenhaComDica;
import gametrack.front.componentes.CampoTextoComDica;
import gametrack.front.componentes.PainelArredondado;
import gametrack.front.componentes.Tema;
import gametrack.front.dto.CadastroUsuarioDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Formulário de cadastro de conta (I_CadastroConta). As mensagens de erro e de sucesso
 * aparecem no próprio formulário, abaixo do botão.
 *
 * @see RF001
 */
public class CadastroContaView extends JPanel {

    private static final String TITULO_TELA = "Crie sua conta";
    private static final String TEXTO_BOTAO = "Cadastrar";
    private static final String SEM_MENSAGEM = " ";
    private static final String MENSAGEM_SUCESSO = "Conta cadastrada com sucesso!";
    private static final String MENSAGEM_ERRO = "Erro inesperado ao cadastrar a conta!";

    private static final int MARGEM = 36;
    private static final int LARGURA_CAMPO = 326;
    private static final int ALTURA_CAMPO = 41;
    private static final int ESPACO = 18;
    private static final int ESPACO_ROTULO = 8;
    private static final float TAMANHO_TITULO = 24f;
    private static final float TAMANHO_ROTULO = 12f;

    private final UsuarioCliente cliente;
    private final CampoTextoComDica campoApelido =
            new CampoTextoComDica("Escolha seu nome de usuário");
    private final CampoSenhaComDica campoSenha = new CampoSenhaComDica("Crie sua senha");
    private final BotaoArredondado botaoCadastrar =
            new BotaoArredondado(TEXTO_BOTAO, Tema.LILAS);
    private final JLabel mensagem = criarRotulo(SEM_MENSAGEM, Font.PLAIN, TAMANHO_ROTULO);
    private Runnable aoCadastrar = () -> { };

    /**
     * Monta a tela.
     *
     * @param cliente acesso às operações de conta do servidor
     */
    public CadastroContaView(UsuarioCliente cliente) {
        super(new GridBagLayout());
        this.cliente = cliente;
        setBackground(Tema.FUNDO_JANELA);
        add(criarCartao());
        campoApelido.addActionListener(evento -> campoSenha.requestFocusInWindow());
        campoSenha.addActionListener(evento -> botaoCadastrar.doClick());
        botaoCadastrar.addActionListener(evento -> cadastrar());
    }

    /**
     * Define o que fazer depois de um cadastro bem-sucedido, como ir para a tela de login.
     *
     * @param acao ação executada após o cadastro
     */
    public void definirAoCadastrar(Runnable acao) {
        this.aoCadastrar = acao;
    }

    private PainelArredondado criarCartao() {
        PainelArredondado cartao =
                new PainelArredondado(Tema.FUNDO_CARTAO_CONTA, Tema.FUNDO_CARTAO_CONTA);
        cartao.setBorder(BorderFactory.createEmptyBorder(MARGEM, MARGEM, ESPACO, MARGEM));
        cartao.add(criarFormulario(), BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.add(criarRotulo(TITULO_TELA, Font.BOLD, TAMANHO_TITULO));
        formulario.add(Box.createVerticalStrut(ESPACO));
        adicionarCampo(formulario, "Nome de usuário", campoApelido);
        adicionarCampo(formulario, "Senha", campoSenha);
        botaoCadastrar.setForeground(Tema.TEXTO_ESCURO);
        ajustarTamanho(botaoCadastrar);
        formulario.add(botaoCadastrar);
        formulario.add(Box.createVerticalStrut(ESPACO_ROTULO));
        formulario.add(mensagem);
        return formulario;
    }

    private void adicionarCampo(JPanel formulario, String texto, JComponent campo) {
        JLabel rotulo = criarRotulo(texto, Font.PLAIN, TAMANHO_ROTULO);
        rotulo.setLabelFor(campo);
        ajustarTamanho(campo);
        formulario.add(rotulo);
        formulario.add(Box.createVerticalStrut(ESPACO_ROTULO));
        formulario.add(campo);
        formulario.add(Box.createVerticalStrut(ESPACO));
    }

    private void ajustarTamanho(JComponent componente) {
        Dimension tamanho = new Dimension(LARGURA_CAMPO, ALTURA_CAMPO);
        componente.setPreferredSize(tamanho);
        componente.setMinimumSize(tamanho);
        componente.setMaximumSize(tamanho);
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void cadastrar() {
        mensagem.setText(SEM_MENSAGEM);
        botaoCadastrar.setEnabled(false);
        new EnvioCadastro(new CadastroUsuarioDTO(campoApelido.getText().trim(),
                new String(campoSenha.getPassword()))).execute();
    }

    private void concluirCadastro() {
        campoApelido.setText("");
        campoSenha.setText("");
        exibirMensagem(MENSAGEM_SUCESSO, Tema.SUCESSO);
        aoCadastrar.run();
    }

    private void exibirErro(Throwable causa) {
        exibirMensagem(causa instanceof ErroServidorException
                ? causa.getMessage() : MENSAGEM_ERRO, Tema.ERRO);
    }

    private void exibirMensagem(String texto, Color cor) {
        mensagem.setForeground(cor);
        mensagem.setText(texto);
    }

    private static JLabel criarRotulo(String texto, int estilo, float tamanho) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Tema.fonte(estilo, tamanho));
        rotulo.setForeground(Tema.TEXTO);
        rotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rotulo;
    }

    /** Envia o cadastro fora da thread da interface, para a tela não travar. */
    private class EnvioCadastro extends SwingWorker<Void, Void> {

        private final CadastroUsuarioDTO dados;

        EnvioCadastro(CadastroUsuarioDTO dados) {
            this.dados = dados;
        }

        @Override
        protected Void doInBackground() {
            cliente.cadastrar(dados);
            return null;
        }

        @Override
        protected void done() {
            botaoCadastrar.setEnabled(true);
            try {
                get();
                concluirCadastro();
            } catch (ExecutionException e) {
                exibirErro(e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
