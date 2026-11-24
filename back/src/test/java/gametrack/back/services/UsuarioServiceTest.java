package gametrack.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gametrack.back.config.Mensagens;
import gametrack.back.model.CadastroUsuarioDTO;
import gametrack.back.model.Usuario;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.seguranca.CriptografiaSenha;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para as regras de cadastro de conta em UsuarioService.
 *
 * @see RF001
 * @see RF001.1
 * @see RF001.2
 * @see RNF005
 * @see RNF006
 */
public class UsuarioServiceTest {

    private UsuarioService usuarioService;

    /**
     * Prepara o serviço com dependências falsas em memória antes de cada teste.
     */
    @BeforeEach
    public void configurarCenario() {
        UsuarioRepository repositoryFalso = new UsuarioRepositoryFalso();
        CriptografiaSenha criptografiaFalsa = senha -> "hash_" + senha;
        this.usuarioService = new UsuarioService(repositoryFalso, criptografiaFalsa);
    }

    /**
     * Valida o fluxo principal de cadastro com apelido único e senha válida.
     */
    @Test
    public void deveCadastrarContaQuandoDadosForemValidos() {
        CadastroUsuarioDTO dados = new CadastroUsuarioDTO("pedro", "senha1234");
        Usuario salvo = usuarioService.cadastrar(dados);

        assertNotNull(salvo.getId());
        assertEquals("pedro", salvo.getApelido());
        assertEquals("hash_senha1234", salvo.getSenhaHash());
    }

    /**
     * Garante que senha vazia ou ausente dispare a mensagem exigida pelo RF001.2.
     */
    @Test
    public void deveLancarExcecaoQuandoSenhaNaoForInformada() {
        CadastroUsuarioDTO dados = new CadastroUsuarioDTO("pedro", "");

        NegocioException erro = assertThrows(
                NegocioException.class, () -> usuarioService.cadastrar(dados));
        assertEquals(Mensagens.SENHA_NAO_INFORMADA, erro.getMessage());
    }

    /**
     * Garante o cumprimento do tamanho mínimo de 8 caracteres exigido pelo RNF005.
     */
    @Test
    public void deveLancarExcecaoQuandoSenhaTiverMenosDeOitoCaracteres() {
        CadastroUsuarioDTO dados = new CadastroUsuarioDTO("pedro", "1234567");

        NegocioException erro = assertThrows(
                NegocioException.class, () -> usuarioService.cadastrar(dados));
        assertEquals(Mensagens.SENHA_TAMANHO_INVALIDO, erro.getMessage());
    }

    /**
     * Garante o bloqueio de apelidos duplicados exigido pelo RF001.1.
     */
    @Test
    public void deveLancarExcecaoQuandoApelidoJaEstiverEmUso() {
        usuarioService.cadastrar(new CadastroUsuarioDTO("pedro", "senha1234"));
        CadastroUsuarioDTO duplicado = new CadastroUsuarioDTO("pedro", "outrasenha");

        NegocioException erro = assertThrows(
                NegocioException.class, () -> usuarioService.cadastrar(duplicado));
        assertEquals(Mensagens.APELIDO_JA_UTILIZADO, erro.getMessage());
    }

    private static final class UsuarioRepositoryFalso implements UsuarioRepository {

        private final List<Usuario> usuarios = new ArrayList<>();

        @Override
        public boolean existePorApelido(String apelido) {
            return usuarios.stream().anyMatch(u -> u.getApelido().equals(apelido));
        }

        @Override
        public Usuario inserir(Usuario usuario) {
            usuario.setId(usuarios.size() + 1);
            usuarios.add(usuario);
            return usuario;
        }
    }
}