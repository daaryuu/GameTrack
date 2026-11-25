package gametrack.front.cliente;

import gametrack.front.dto.CredenciaisDTO;
import gametrack.front.dto.Perfil;
import gametrack.front.dto.SessaoDTO;

/**
 * Implementação de {@link SessaoCliente} que fala com o servidor por HTTP. O token recebido
 * fica na conexão, e os outros clientes que a compartilham passam a enviá-lo.
 *
 * @see RF002
 */
public class SessaoClienteHttp implements SessaoCliente {

    private static final String CAMINHO_SESSOES = "/sessoes";
    private static final String MENSAGEM_SEM_TOKEN = "Erro inesperado no servidor!";

    private final ConexaoHttp conexao;

    /**
     * Cria o cliente.
     *
     * @param conexao conexão com o servidor, compartilhada entre os clientes
     */
    public SessaoClienteHttp(ConexaoHttp conexao) {
        this.conexao = conexao;
    }

    @Override
    public Perfil autenticar(CredenciaisDTO credenciais) {
        SessaoDTO sessao = conexao.post(CAMINHO_SESSOES, credenciais, SessaoDTO.class);
        if (sessao == null || sessao.token() == null || sessao.token().isBlank()) {
            throw new ErroServidorException(MENSAGEM_SEM_TOKEN);
        }
        conexao.definirToken(sessao.token());
        // Sem um perfil conhecido, vale o de menor acesso.
        return sessao.perfil() != null ? sessao.perfil() : Perfil.USUARIO;
    }
}
