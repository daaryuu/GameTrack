package gametrack.front.view;

import gametrack.front.cliente.UsuarioCliente;
import gametrack.front.componentes.PainelArredondado;
import gametrack.front.componentes.Tema;
import gametrack.front.dto.CadastroUsuarioDTO;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Formulário de cadastro de conta (I_CadastroConta). As mensagens de erro e de sucesso
 * aparecem no próprio formulário, abaixo do botão.
 *
 * @see RF001
 */
public class CadastroContaView extends JPanel {

    private static final FormularioConta.Textos TEXTOS = new FormularioConta.Textos(
            "Crie sua conta", "Escolha seu nome de usuário", "Crie sua senha", "Cadastrar");
    private static final String MENSAGEM_SUCESSO = "Conta cadastrada com sucesso!";
    private static final int MARGEM = 36;
    private static final int MARGEM_INFERIOR = 18;
    private static final int ESPACO_TITULO = 18;

    private final UsuarioCliente cliente;
    private final FormularioConta formulario = new FormularioConta(TEXTOS, ESPACO_TITULO);
    private final ConviteComLink convite =
            new ConviteComLink("Já tem uma conta?", "Entrar");
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
        formulario.definirAoEnviar(this::cadastrar);
        formulario.limparAoEsconder(this);
    }

    /**
     * Define o que fazer quando o usuário escolhe voltar para o login.
     *
     * @param acao ação que abre a tela de login
     */
    public void definirAoVoltar(Runnable acao) {
        convite.definirAoClicar(acao);
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
        cartao.setBorder(
                BorderFactory.createEmptyBorder(MARGEM, MARGEM, MARGEM_INFERIOR, MARGEM));
        cartao.add(formulario, BorderLayout.CENTER);
        convite.setBorder(BorderFactory.createEmptyBorder(MARGEM_INFERIOR, 0, 0, 0));
        cartao.add(convite, BorderLayout.SOUTH);
        return cartao;
    }

    private void cadastrar() {
        CadastroUsuarioDTO dados =
                new CadastroUsuarioDTO(formulario.apelido(), formulario.senha());
        formulario.enviar(() -> cliente.cadastrar(dados), this::concluirCadastro);
    }

    private void concluirCadastro() {
        formulario.limpar();
        formulario.exibirSucesso(MENSAGEM_SUCESSO);
        aoCadastrar.run();
    }
}
