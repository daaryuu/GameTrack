package gametrack.front.cliente;

import gametrack.front.dto.CadastroUsuarioDTO;

/**
 * Implementação de {@link UsuarioCliente} que fala com o servidor por HTTP.
 *
 * @see RF001
 */
public class UsuarioClienteHttp implements UsuarioCliente {

    private static final String CAMINHO_USUARIOS = "/usuarios";

    private final ConexaoHttp conexao;

    /**
     * Cria o cliente.
     *
     * @param urlBase endereço do servidor, como {@code http://localhost:8080}
     */
    public UsuarioClienteHttp(String urlBase) {
        this.conexao = new ConexaoHttp(urlBase);
    }

    @Override
    public void cadastrar(CadastroUsuarioDTO dados) {
        conexao.post(CAMINHO_USUARIOS, dados);
    }
}
