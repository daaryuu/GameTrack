package gametrack.back.services;

import gametrack.back.config.Mensagens;
import gametrack.back.model.CredenciaisDTO;
import gametrack.back.model.SessaoDTO;
import gametrack.back.model.Usuario;
import gametrack.back.repositories.UsuarioRepository;
import gametrack.back.seguranca.AutenticacaoException;
import gametrack.back.seguranca.SessoesEmMemoria;
import gametrack.back.seguranca.VerificadorSenha;

/**
 * Executa as regras de autenticação do usuário.
 *
 * @see RF002
 */
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final VerificadorSenha verificadorSenha;
    private final SessoesEmMemoria sessoes;

    /**
     * Injeta as dependências da autenticação.
     *
     * @param usuarioRepository interface de persistência de usuários
     * @param verificadorSenha interface de conferência da senha
     * @param sessoes controle de sessões
     */
    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            VerificadorSenha verificadorSenha,
            SessoesEmMemoria sessoes) {
        this.usuarioRepository = usuarioRepository;
        this.verificadorSenha = verificadorSenha;
        this.sessoes = sessoes;
    }

    /**
     * Confere as credenciais e abre a sessão. A mensagem de erro não diz se o que está errado
     * é o apelido ou a senha, como pede o RF002.
     *
     * @param credenciais apelido e senha informados
     * @return sessão aberta
     * @throws AutenticacaoException se as credenciais estiverem incorretas
     */
    public SessaoDTO autenticar(CredenciaisDTO credenciais) {
        if (!temCredenciais(credenciais)) {
            throw credenciaisIncorretas();
        }
        Usuario usuario = usuarioRepository.buscarPorApelido(credenciais.apelido().trim())
                .filter(conta -> verificadorSenha.eSenhaCorreta(
                        credenciais.senha(), conta.getSenhaHash()))
                .orElseThrow(this::credenciaisIncorretas);
        return new SessaoDTO(sessoes.abrir(usuario.getId()));
    }

    private boolean temCredenciais(CredenciaisDTO credenciais) {
        return credenciais != null
                && credenciais.apelido() != null && !credenciais.apelido().isBlank()
                && credenciais.senha() != null && !credenciais.senha().isEmpty();
    }

    private AutenticacaoException credenciaisIncorretas() {
        return new AutenticacaoException(Mensagens.CREDENCIAIS_INCORRETAS);
    }
}
