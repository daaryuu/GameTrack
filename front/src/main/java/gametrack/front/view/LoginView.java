package gametrack.front.view;

import gametrack.front.cliente.SessaoCliente;
import gametrack.front.componentes.Tema;
import gametrack.front.dto.CredenciaisDTO;
import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Tela de acesso ao sistema (I_Login), com apelido e senha e o atalho para o cadastro.
 *
 * @see RF001
 * @see RF002
 */
public class LoginView extends JPanel {

    private static final FormularioConta.Textos TEXTOS = new FormularioConta.Textos(
            "Entre no GameTrack", "Seu nome de usuário", "Digite sua senha",
            "Entrar no GameTrack");
    private static final int ESPACO_TITULO = 44;
    private static final int ESPACO_RODAPE = 36;

    private final SessaoCliente cliente;
    private final FormularioConta formulario = new FormularioConta(TEXTOS, ESPACO_TITULO);
    private final ConviteComLink convite =
            new ConviteComLink("Ainda não tem uma conta?", "Cadastre-se");
    private Runnable aoEntrar = () -> { };

    /**
     * Monta a tela.
     *
     * @param cliente acesso à autenticação do servidor
     */
    public LoginView(SessaoCliente cliente) {
        super(new GridBagLayout());
        this.cliente = cliente;
        setBackground(Tema.FUNDO_LOGIN);
        formulario.definirFundoDosCampos(Tema.FUNDO_CARTAO_CONTA);
        formulario.definirAoEnviar(this::entrar);
        formulario.limparAoEsconder(this);
        add(criarConteudo());
    }

    /**
     * Define o que fazer depois da autenticação, como abrir a biblioteca.
     *
     * @param acao ação executada após a autenticação
     */
    public void definirAoEntrar(Runnable acao) {
        this.aoEntrar = acao;
    }

    /**
     * Define o que fazer quando o usuário escolhe se cadastrar.
     *
     * @param acao ação que abre o cadastro de conta
     */
    public void definirAoCadastrar(Runnable acao) {
        convite.definirAoClicar(acao);
    }

    /**
     * Limpa o formulário e exibe uma mensagem de sucesso, como a de conta cadastrada.
     *
     * @param texto mensagem exibida
     */
    public void exibirSucesso(String texto) {
        formulario.limpar();
        formulario.exibirSucesso(texto);
    }

    /**
     * Limpa o formulário e exibe uma mensagem de erro, como a de sessão expirada.
     *
     * @param texto mensagem exibida
     */
    public void exibirErro(String texto) {
        formulario.limpar();
        formulario.exibirErro(texto);
    }

    private JPanel criarConteudo() {
        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        formulario.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(formulario);
        conteudo.add(Box.createVerticalStrut(ESPACO_RODAPE));
        conteudo.add(convite);
        return conteudo;
    }

    private void entrar() {
        CredenciaisDTO credenciais =
                new CredenciaisDTO(formulario.apelido(), formulario.senha());
        formulario.enviar(() -> cliente.autenticar(credenciais), this::concluirEntrada);
    }

    private void concluirEntrada() {
        formulario.limpar();
        aoEntrar.run();
    }
}
