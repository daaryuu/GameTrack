package gametrack.front.cliente;

import gametrack.front.dto.CadastroUsuarioDTO;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Implementação de {@link UsuarioCliente} que guarda os apelidos em memória e imita as
 * respostas do servidor. Serve para desenvolver e testar as telas sem o servidor.
 *
 * @see RF001
 */
public class UsuarioClienteFalso implements UsuarioCliente {

    private static final int TAMANHO_MINIMO_SENHA = 8;
    private static final String APELIDO_NAO_INFORMADO = "Informe o apelido!";
    private static final String APELIDO_JA_UTILIZADO = "Apelido já utilizado!";
    private static final String SENHA_NAO_INFORMADA = "Informe a senha!";
    private static final String SENHA_TAMANHO_INVALIDO =
            "A senha deve ter no mínimo 8 caracteres!";

    private final Set<String> apelidos = new HashSet<>();

    @Override
    public synchronized void cadastrar(CadastroUsuarioDTO dados) {
        validar(dados);
        if (!apelidos.add(dados.apelido().trim().toLowerCase(Locale.ROOT))) {
            throw new ErroServidorException(APELIDO_JA_UTILIZADO);
        }
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
