package gametrack.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gametrack.back.config.Mensagens;
import gametrack.back.model.CadastroUsuarioDTO;
import gametrack.back.model.Perfil;
import gametrack.back.model.Usuario;
import gametrack.back.repositories.RegistroDuplicadoException;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.seguranca.BcryptCriptografiaSenha;
import gametrack.back.seguranca.CriptografiaSenha;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    /**
     * Garante o tamanho do apelido definido no dicionário de dados (3 a 30 caracteres).
     */
    @Test
    public void deveLancarExcecaoQuandoApelidoTiverTamanhoInvalido() {
        for (String apelido : List.of(" ab ", "a".repeat(31))) {
            NegocioException erro = assertThrows(NegocioException.class,
                    () -> usuarioService.cadastrar(
                            new CadastroUsuarioDTO(apelido, "senha1234")));
            assertEquals(Mensagens.APELIDO_TAMANHO_INVALIDO, erro.getMessage());
        }
    }

    /**
     * Garante que o limite de senha aceito pelo serviço é o mesmo da biblioteca bcrypt,
     * contado em bytes (cada "é" ocupa 2).
     */
    @Test
    public void deveRespeitarOLimiteDeSenhaDoBcrypt() {
        usuarioService = new UsuarioService(
                new UsuarioRepositoryFalso(), new BcryptCriptografiaSenha());

        assertNotNull(usuarioService.cadastrar(
                new CadastroUsuarioDTO("pedro", "a".repeat(71))).getId());
        for (String senha : List.of("a".repeat(72), "é".repeat(36))) {
            NegocioException erro = assertThrows(NegocioException.class,
                    () -> usuarioService.cadastrar(new CadastroUsuarioDTO("maria", senha)));
            assertEquals(Mensagens.SENHA_MUITO_LONGA, erro.getMessage());
        }
    }

    /**
     * Garante a mensagem do RF001.1 quando o apelido é usado entre a verificação e a inserção.
     */
    @Test
    public void deveInformarApelidoEmUsoQuandoOBancoRecusarDuplicado() {
        UsuarioRepository repositorio = new UsuarioRepository() {
            @Override
            public boolean existePorApelido(String apelido) {
                return false;
            }

            @Override
            public Optional<Usuario> buscarPorApelido(String apelido) {
                return Optional.empty();
            }

            @Override
            public Usuario inserir(Usuario usuario) {
                throw new RegistroDuplicadoException(new RuntimeException("duplicado"));
            }
        };
        usuarioService = new UsuarioService(repositorio, senha -> "hash");

        NegocioException erro = assertThrows(NegocioException.class,
                () -> usuarioService.cadastrar(new CadastroUsuarioDTO("pedro", "senha1234")));
        assertEquals(Mensagens.APELIDO_JA_UTILIZADO, erro.getMessage());
    }

    /**
     * Garante que o administrador seja criado uma única vez, com a senha inicial em hash.
     */
    @Test
    public void deveCriarOAdministradorUmaUnicaVez() {
        UsuarioRepositoryFalso repositorio = new UsuarioRepositoryFalso();
        usuarioService = new UsuarioService(repositorio, senha -> "hash_" + senha);

        usuarioService.garantirAdministrador(() -> "senha1234");
        usuarioService.garantirAdministrador(() -> {
            throw new AssertionError("A senha não deveria ser consultada de novo.");
        });

        assertEquals(1, repositorio.usuarios.size());
        assertEquals("admin", repositorio.usuarios.get(0).getApelido());
        assertEquals("hash_senha1234", repositorio.usuarios.get(0).getSenhaHash());
        assertEquals(Perfil.ADMINISTRADOR, repositorio.usuarios.get(0).getPerfil());
    }

    /**
     * Garante que uma conta comum com o apelido admin não ganhe acesso de administrador.
     */
    @Test
    public void naoDevePromoverContaComumAAdministrador() {
        Usuario comum = usuarioService.cadastrar(new CadastroUsuarioDTO("admin", "senha1234"));

        assertThrows(IllegalStateException.class,
                () -> usuarioService.garantirAdministrador(() -> "outra1234"));
        assertEquals(Perfil.USUARIO, comum.getPerfil());
    }

    /**
     * Garante que a senha inicial do administrador siga o RNF005.
     */
    @Test
    public void deveValidarASenhaInicialDoAdministrador() {
        assertThrows(NegocioException.class,
                () -> usuarioService.garantirAdministrador(() -> "1234567"));
    }

    private static final class UsuarioRepositoryFalso implements UsuarioRepository {

        private final List<Usuario> usuarios = new ArrayList<>();

        @Override
        public boolean existePorApelido(String apelido) {
            return buscarPorApelido(apelido).isPresent();
        }

        @Override
        public Optional<Usuario> buscarPorApelido(String apelido) {
            return usuarios.stream()
                    .filter(usuario -> usuario.getApelido().equals(apelido))
                    .findFirst();
        }

        @Override
        public Usuario inserir(Usuario usuario) {
            usuario.setId(usuarios.size() + 1);
            usuarios.add(usuario);
            return usuario;
        }
    }
}
