package gametrack.front.cliente;

import gametrack.front.dto.CadastroUsuarioDTO;
import gametrack.front.dto.CredenciaisDTO;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Implementação de {@link UsuarioCliente} e {@link SessaoCliente} que guarda as contas em
 * memória e imita as respostas do servidor. Serve para desenvolver e testar as telas sem o
 * servidor. As contas somem ao fechar o programa.
 *
 * @see RF001
 * @see RF002
 */
public class UsuarioClienteFalso implements UsuarioCliente, SessaoCliente {

    private static final int TAMANHO_MINIMO_SENHA = 8;
    private static final String APELIDO_NAO_INFORMADO = "Informe o apelido!";
    private static final String APELIDO_JA_UTILIZADO = "Apelido já utilizado!";
    private static final String SENHA_NAO_INFORMADA = "Informe a senha!";
    private static final String SENHA_TAMANHO_INVALIDO =
            "A senha deve ter no mínimo 8 caracteres!";
    private static final String CREDENCIAIS_INCORRETAS = "Apelido ou senha incorretos!";

    private final Map<String, String> senhasPorApelido = new HashMap<>();

    @Override
    public synchronized void cadastrar(CadastroUsuarioDTO dados) {
        validar(dados);
        if (senhasPorApelido.putIfAbsent(normalizar(dados.apelido()), dados.senha()) != null) {
            throw new ErroServidorException(APELIDO_JA_UTILIZADO);
        }
    }

    @Override
    public synchronized void autenticar(CredenciaisDTO credenciais) {
        String senha = senhasPorApelido.get(normalizar(credenciais.apelido()));
        if (senha == null || !senha.equals(credenciais.senha())) {
            throw new ErroServidorException(CREDENCIAIS_INCORRETAS);
        }
    }

    private String normalizar(String apelido) {
        return apelido.trim().toLowerCase(Locale.ROOT);
    }

    private void validar(CadastroUsuarioDTO dados) {
        if (dados.apelido().isBlank()) {
            throw new ErroServidorException(APELIDO_NAO_INFORMADO);
        }
        if (dados.senha().isBlank()) {
            throw new ErroServidorException(SENHA_NAO_INFORMADA);
        }
        if (dados.senha().length() < TAMANHO_MINIMO_SENHA) {
            throw new ErroServidorException(SENHA_TAMANHO_INVALIDO);
        }
    }
}
