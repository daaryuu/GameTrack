package gametrack.front.view;

import gametrack.front.cliente.JogoCliente;
import gametrack.front.cliente.SessaoCliente;
import gametrack.front.cliente.UsuarioCliente;
import gametrack.front.dto.Perfil;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * Reúne as telas e controla a navegação entre elas: começa no login, vai ao cadastro de conta
 * e, depois da autenticação, abre o catálogo, de onde o administrador chega ao cadastro e à
 * alteração de jogo. Se a sessão expirar, volta ao login.
 *
 * @see RF001
 * @see RF002
 * @see RF003
 * @see RF005
 * @see RNF007
 */
public class PrincipalView extends JPanel {

    private static final String LOGIN = "login";
    private static final String CADASTRO = "cadastro";
    private static final String CATALOGO = "catalogo";
    private static final String JOGO = "jogo";
    private static final String JOGO_CADASTRADO = "Jogo cadastrado com sucesso!";
    private static final String JOGO_ALTERADO = "Jogo alterado com sucesso!";
    private static final String CONTA_CADASTRADA = "Conta cadastrada com sucesso!";
    private static final String SESSAO_EXPIRADA = "Sua sessão expirou. Entre novamente.";

    private final CardLayout layout = new CardLayout();
    private final JogoCliente jogos;
    private final LoginView login;

    /**
     * Monta as telas e exibe o login.
     *
     * @param usuarios acesso às operações de conta
     * @param sessoes acesso à autenticação
     * @param jogos acesso ao catálogo
     */
    public PrincipalView(UsuarioCliente usuarios, SessaoCliente sessoes, JogoCliente jogos) {
        this.jogos = jogos;
        this.login = new LoginView(sessoes);
        setLayout(layout);
        add(login, LOGIN);
        add(criarCadastro(usuarios), CADASTRO);
        login.definirAoCadastrar(() -> layout.show(this, CADASTRO));
        login.definirAoEntrar(this::abrirCatalogo);
    }

    private CadastroContaView criarCadastro(UsuarioCliente usuarios) {
        CadastroContaView cadastro = new CadastroContaView(usuarios);
        cadastro.definirAoVoltar(() -> layout.show(this, LOGIN));
        cadastro.definirAoCadastrar(() -> {
            login.exibirSucesso(CONTA_CADASTRADA);
            layout.show(this, LOGIN);
        });
        return cadastro;
    }

    private void abrirCatalogo(Perfil perfil) {
        // A tela é criada só agora porque carrega os jogos ao ser criada, o que exige a sessão.
        CatalogoListaView catalogo = new CatalogoListaView(jogos, perfil);
        catalogo.definirAoExpirarSessao(() -> encerrar(catalogo));
        catalogo.definirAoAdicionar(() -> abrirFormulario(
                catalogo, JogoFormView.paraCadastro(jogos), JOGO_CADASTRADO));
        catalogo.definirAoAlterar(jogo -> abrirFormulario(
                catalogo, JogoFormView.paraAlteracao(jogos, jogo.id()), JOGO_ALTERADO));
        add(catalogo, CATALOGO);
        layout.show(this, CATALOGO);
    }

    private void abrirFormulario(
            CatalogoListaView catalogo, JogoFormView formulario, String mensagemAoSalvar) {
        formulario.definirAoCancelar(() -> fechar(formulario));
        formulario.definirAoSalvar(() -> {
            fechar(formulario);
            catalogo.recarregar(mensagemAoSalvar);
        });
        formulario.definirAoExpirarSessao(() -> {
            fechar(formulario);
            encerrar(catalogo);
        });
        add(formulario, JOGO);
        layout.show(this, JOGO);
    }

    private void fechar(JogoFormView formulario) {
        // Remover a tela visível faria o CardLayout exibir o login por um instante e limpá-lo.
        layout.show(this, CATALOGO);
        remove(formulario);
    }

    private void encerrar(CatalogoListaView catalogo) {
        // Uma resposta atrasada de um catálogo já fechado não pode derrubar a sessão nova.
        if (catalogo.getParent() != this) {
            return;
        }
        login.exibirErro(SESSAO_EXPIRADA);
        layout.show(this, LOGIN);
        remove(catalogo);
    }
}
