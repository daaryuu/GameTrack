package gametrack.back.seguranca;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Cria e valida as sessões dos usuários, guardadas na memória do servidor. Uma sessão expira
 * depois de um tempo sem uso (RNF007), e todas se perdem quando o servidor é reiniciado.
 *
 * @see RF002
 * @see RNF007
 */
public class SessoesEmMemoria implements VerificadorAutenticacao {

    private static final int TAMANHO_TOKEN_BYTES = 32;
    private static final String PREFIXO_CREDENCIAL = "Bearer ";

    private final Map<String, Sessao> sessoes = new HashMap<>();
    private final SecureRandom sorteador = new SecureRandom();
    private final Duration tempoSemUso;
    private final Clock relogio;

    /**
     * Cria o controle de sessões.
     *
     * @param tempoSemUso tempo sem uso até a sessão expirar
     * @param relogio fonte da hora atual
     */
    public SessoesEmMemoria(Duration tempoSemUso, Clock relogio) {
        this.tempoSemUso = tempoSemUso;
        this.relogio = relogio;
    }

    /**
     * Abre uma sessão para um usuário já autenticado.
     *
     * @param usuarioId identificador do usuário
     * @return token da sessão
     */
    public synchronized String abrir(int usuarioId) {
        sessoes.values().removeIf(this::eExpirada);
        String token = gerarToken();
        sessoes.put(token, new Sessao(usuarioId, relogio.instant()));
        return token;
    }

    /**
     * Valida a credencial no formato {@code Bearer <token>} e renova o tempo da sessão.
     *
     * @param credencial valor do cabeçalho de autenticação
     * @return identificador do usuário da sessão
     * @throws AutenticacaoException se a sessão não existir ou tiver expirado
     */
    @Override
    public synchronized Integer exigirUsuarioAutenticado(String credencial) {
        if (credencial == null || !credencial.startsWith(PREFIXO_CREDENCIAL)) {
            throw new AutenticacaoException();
        }
        String token = credencial.substring(PREFIXO_CREDENCIAL.length());
        Sessao sessao = sessoes.get(token);
        if (sessao == null || eExpirada(sessao)) {
            sessoes.remove(token);
            throw new AutenticacaoException();
        }
        sessoes.put(token, new Sessao(sessao.usuarioId(), relogio.instant()));
        return sessao.usuarioId();
    }

    private boolean eExpirada(Sessao sessao) {
        return !relogio.instant().isBefore(sessao.ultimoUso().plus(tempoSemUso));
    }

    private String gerarToken() {
        byte[] bytes = new byte[TAMANHO_TOKEN_BYTES];
        sorteador.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private record Sessao(int usuarioId, Instant ultimoUso) {
    }
}
