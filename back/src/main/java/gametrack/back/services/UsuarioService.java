package gametrack.back.services;

import gametrack.back.config.Mensagens;
import gametrack.back.model.CadastroUsuarioDTO;
import gametrack.back.model.Usuario;
import gametrack.back.repositories.RegistroDuplicadoException;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.seguranca.CriptografiaSenha;
import java.nio.charset.StandardCharsets;

/**
 * Executa as regras de negócio referentes ao cadastro e gerenciamento de contas.
 *
 * @see RF001
 * @see RF001.1
 * @see RF001.2
 * @see RNF005
 * @see RNF006
 */
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CriptografiaSenha criptografiaSenha;

    /**
     * Injeta as dependências necessárias para o serviço de usuários.
     *
     * @param usuarioRepository interface de persistência de usuários
     * @param criptografiaSenha interface de geração de hash bcrypt
     */
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            CriptografiaSenha criptografiaSenha) {
        this.usuarioRepository = usuarioRepository;
        this.criptografiaSenha = criptografiaSenha;
    }

    /**
     * Valida os dados, aplica as regras de unicidade e segurança e cadastra a conta.
     *
     * @param dados dados de apelido e senha informados no cadastro
     * @return usuário recém-cadastrado
     * @throws NegocioException se apelido ou senha violarem as regras do requisito
     */
    public Usuario cadastrar(CadastroUsuarioDTO dados) {
        validarDadosObrigatorios(dados);
        String apelidoLimpo = dados.apelido().trim();
        verificarDisponibilidadeApelido(apelidoLimpo);
        String senhaHash = criptografiaSenha.gerarHash(dados.senha());
        Usuario novoUsuario = new Usuario(null, apelidoLimpo, senhaHash);
        return inserir(novoUsuario);
    }

    private Usuario inserir(Usuario usuario) {
        try {
            return usuarioRepository.inserir(usuario);
        } catch (RegistroDuplicadoException e) {
            // Outra requisição pode ter usado o apelido depois da verificação (RF001.1).
            throw new NegocioException(Mensagens.APELIDO_JA_UTILIZADO);
        }
    }

    private void validarDadosObrigatorios(CadastroUsuarioDTO dados) {
        if (dados == null || dados.apelido() == null || dados.apelido().isBlank()) {
            throw new NegocioException(Mensagens.APELIDO_NAO_INFORMADO);
        }
        validarTamanhoApelido(dados.apelido().trim());
        validarSenha(dados.senha());
    }

    private void validarTamanhoApelido(String apelido) {
        if (apelido.length() < Mensagens.TAMANHO_MINIMO_APELIDO
                || apelido.length() > Mensagens.TAMANHO_MAXIMO_APELIDO) {
            throw new NegocioException(Mensagens.APELIDO_TAMANHO_INVALIDO);
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new NegocioException(Mensagens.SENHA_NAO_INFORMADA);
        }
        if (senha.length() < Mensagens.TAMANHO_MINIMO_SENHA) {
            throw new NegocioException(Mensagens.SENHA_TAMANHO_INVALIDO);
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length
                > Mensagens.TAMANHO_MAXIMO_SENHA_BYTES) {
            throw new NegocioException(Mensagens.SENHA_MUITO_LONGA);
        }
    }

    private void verificarDisponibilidadeApelido(String apelido) {
        if (usuarioRepository.existePorApelido(apelido)) {
            throw new NegocioException(Mensagens.APELIDO_JA_UTILIZADO);
        }
    }
}