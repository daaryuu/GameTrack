package gametrack.front.view;

import gametrack.front.cliente.ErroServidorException;
import gametrack.front.componentes.BotaoArredondado;
import gametrack.front.componentes.CampoSenhaComDica;
import gametrack.front.componentes.CampoTextoComDica;
import gametrack.front.componentes.Tema;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Formulário de apelido e senha comum às telas de conta. As mensagens aparecem abaixo do
 * botão, e o envio roda fora da thread da interface, para a tela não travar.
 *
 * @see RF001
 * @see RF002
 */
class FormularioConta extends JPanel {

    private static final String SEM_MENSAGEM = " ";
    private static final String MENSAGEM_ERRO = "Erro inesperado!";
    private static final int LARGURA_CAMPO = 326;
    private static final int ALTURA_CAMPO = 41;
    private static final int ESPACO = 18;
    private static final int ESPACO_ROTULO = 8;
    private static final float TAMANHO_TITULO = 24f;
    private static final float TAMANHO_ROTULO = 12f;

    private final CampoTextoComDica campoApelido;
    private final CampoSenhaComDica campoSenha;
    private final BotaoArredondado botao;
    private final JLabel mensagem = criarRotulo(SEM_MENSAGEM, Font.PLAIN, TAMANHO_ROTULO);

    FormularioConta(Textos textos, int espacoTitulo) {
        this.campoApelido = new CampoTextoComDica(textos.dicaApelido());
        this.campoSenha = new CampoSenhaComDica(textos.dicaSenha());
        this.botao = new BotaoArredondado(textos.botao(), Tema.LILAS);
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(criarRotulo(textos.titulo(), Font.BOLD, TAMANHO_TITULO));
        add(Box.createVerticalStrut(espacoTitulo));
        adicionarCampo("Nome de usuário", campoApelido);
        adicionarCampo("Senha", campoSenha);
        adicionarBotao();
        add(Box.createVerticalStrut(ESPACO_ROTULO));
        add(mensagem);
        campoApelido.addActionListener(evento -> campoSenha.requestFocusInWindow());
        campoSenha.addActionListener(evento -> botao.doClick());
    }

    String apelido() {
        return campoApelido.getText().trim();
    }

    String senha() {
        return new String(campoSenha.getPassword());
    }

    void definirFundoDosCampos(Color cor) {
        campoApelido.setBackground(cor);
        campoSenha.setBackground(cor);
    }

    void definirAoEnviar(Runnable acao) {
        botao.addActionListener(evento -> acao.run());
    }

    void enviar(Runnable tarefa, Runnable aoConcluir) {
        mensagem.setText(SEM_MENSAGEM);
        botao.setEnabled(false);
        new Envio(tarefa, aoConcluir).execute();
    }

    void limpar() {
        campoApelido.setText("");
        campoSenha.setText("");
        mensagem.setText(SEM_MENSAGEM);
    }

    void limparAoEsconder(Component tela) {
        tela.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent evento) {
                limpar();
            }
        });
    }

    void exibirSucesso(String texto) {
        exibirMensagem(texto, Tema.SUCESSO);
    }

    private void adicionarCampo(String texto, JComponent campo) {
        JLabel rotulo = criarRotulo(texto, Font.PLAIN, TAMANHO_ROTULO);
        rotulo.setLabelFor(campo);
        ajustarTamanho(campo);
        add(rotulo);
        add(Box.createVerticalStrut(ESPACO_ROTULO));
        add(campo);
        add(Box.createVerticalStrut(ESPACO));
    }

    private void adicionarBotao() {
        botao.setForeground(Tema.TEXTO_ESCURO);
        ajustarTamanho(botao);
        add(botao);
    }

    private void ajustarTamanho(JComponent componente) {
        Dimension tamanho = new Dimension(LARGURA_CAMPO, ALTURA_CAMPO);
        componente.setPreferredSize(tamanho);
        componente.setMinimumSize(tamanho);
        componente.setMaximumSize(tamanho);
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    /** Textos que mudam entre as telas de conta. */
    record Textos(String titulo, String dicaApelido, String dicaSenha, String botao) {
    }

    private class Envio extends SwingWorker<Void, Void> {

        private final Runnable tarefa;
        private final Runnable aoConcluir;

        Envio(Runnable tarefa, Runnable aoConcluir) {
            this.tarefa = tarefa;
            this.aoConcluir = aoConcluir;
        }

        @Override
        protected Void doInBackground() {
            tarefa.run();
            return null;
        }

        @Override
        protected void done() {
            botao.setEnabled(true);
            try {
                get();
                aoConcluir.run();
            } catch (ExecutionException e) {
                exibirErro(e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
