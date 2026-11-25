package gametrack.back.services;

import gametrack.back.config.Mensagens;
import gametrack.back.model.DadosJogoDTO;
import gametrack.back.model.FiltroJogoDTO;
import gametrack.back.model.Jogo;
import gametrack.back.model.JogoDTO;
import gametrack.back.model.Perfil;
import gametrack.back.repositories.JogoRepository;
import gametrack.back.repositories.PerfilRepository;
import gametrack.back.seguranca.AutenticacaoException;
import gametrack.back.seguranca.PermissaoException;
import java.util.List;

/**
 * Implementa as regras de negócio do catálogo de jogos. A consulta é aberta a todos os
 * usuários autenticados, e o cadastro e a alteração são exclusivos do administrador.
 *
 * @see RF003
 * @see RF003.1
 * @see RF004
 * @see RF004.1
 * @see RF005
 */
public class JogoService {

    private final JogoRepository jogoRepository;
    private final PerfilRepository perfilRepository;

    /**
     * Constrói o serviço injetando os repositórios.
     *
     * @param jogoRepository repositório de acesso ao catálogo
     * @param perfilRepository repositório de consulta do perfil das contas
     */
    public JogoService(JogoRepository jogoRepository, PerfilRepository perfilRepository) {
        this.jogoRepository = jogoRepository;
        this.perfilRepository = perfilRepository;
    }

    /**
     * Lista os jogos do catálogo conforme os filtros informados para um usuário autenticado.
     *
     * @param usuarioAutenticadoId identificador do usuário autenticado na sessão
     * @param filtro critérios opcionais de filtragem
     * @return lista de DTOs dos jogos encontrados, vazia quando nenhum jogo atende ao filtro
     * @throws NegocioException se o ano do filtro não for numérico
     * @throws AutenticacaoException se não houver usuário autenticado
     */
    public List<JogoDTO> listarCatalogo(Integer usuarioAutenticadoId, FiltroJogoDTO filtro) {
        validarUsuarioAutenticado(usuarioAutenticadoId);
        FiltroJogoDTO filtroEfetivo = (filtro != null) ? filtro : FiltroJogoDTO.semFiltro();
        Integer anoConvertido = converterAno(filtroEfetivo.ano());

        List<Jogo> jogos = jogoRepository.listarComFiltros(
                filtroEfetivo.titulo(),
                filtroEfetivo.genero(),
                filtroEfetivo.desenvolvedora(),
                anoConvertido);

        return jogos.stream().map(JogoDTO::deEntidade).toList();
    }

    /**
     * Cadastra um jogo no catálogo.
     *
     * @param usuarioAutenticadoId identificador do usuário autenticado na sessão
     * @param dados dados do jogo
     * @throws AutenticacaoException se não houver usuário autenticado
     * @throws PermissaoException se o usuário não for administrador
     * @throws NegocioException se algum dado faltar ou estiver fora dos limites
     */
    public void cadastrar(Integer usuarioAutenticadoId, DadosJogoDTO dados) {
        validarAdministrador(usuarioAutenticadoId);
        jogoRepository.inserir(montarJogo(null, dados));
    }

    /**
     * Busca um jogo do catálogo.
     *
     * @param usuarioAutenticadoId identificador do usuário autenticado na sessão
     * @param jogoId identificador do jogo
     * @return dados do jogo
     * @throws AutenticacaoException se não houver usuário autenticado
     * @throws NaoEncontradoException se o jogo não existir
     */
    public JogoDTO buscarPorId(Integer usuarioAutenticadoId, int jogoId) {
        validarUsuarioAutenticado(usuarioAutenticadoId);
        return jogoRepository.buscarPorId(jogoId)
                .map(JogoDTO::deEntidade)
                .orElseThrow(this::jogoNaoEncontrado);
    }

    /**
     * Altera os dados de um jogo do catálogo.
     *
     * @param usuarioAutenticadoId identificador do usuário autenticado na sessão
     * @param jogoId identificador do jogo
     * @param dados novos dados do jogo, todos obrigatórios como no cadastro
     * @throws AutenticacaoException se não houver usuário autenticado
     * @throws PermissaoException se o usuário não for administrador
     * @throws NegocioException se algum dado faltar ou estiver fora dos limites
     * @throws NaoEncontradoException se o jogo não existir
     */
    public void alterar(Integer usuarioAutenticadoId, int jogoId, DadosJogoDTO dados) {
        validarAdministrador(usuarioAutenticadoId);
        if (!jogoRepository.atualizar(montarJogo(jogoId, dados))) {
            throw jogoNaoEncontrado();
        }
    }

    private NaoEncontradoException jogoNaoEncontrado() {
        return new NaoEncontradoException(Mensagens.JOGO_NAO_ENCONTRADO);
    }

    private Jogo montarJogo(Integer jogoId, DadosJogoDTO dados) {
        validarDadosInformados(dados);
        Jogo jogo = new Jogo(jogoId, dados.titulo().trim(), dados.genero().trim(),
                dados.desenvolvedora().trim(), dados.ano());
        validarLimites(jogo);
        return jogo;
    }

    private void validarAdministrador(Integer usuarioAutenticadoId) {
        validarUsuarioAutenticado(usuarioAutenticadoId);
        boolean administrador = perfilRepository.buscarPerfil(usuarioAutenticadoId)
                .filter(perfil -> perfil == Perfil.ADMINISTRADOR)
                .isPresent();
        if (!administrador) {
            throw new PermissaoException();
        }
    }

    private void validarUsuarioAutenticado(Integer usuarioAutenticadoId) {
        if (usuarioAutenticadoId == null || usuarioAutenticadoId <= 0) {
            throw new AutenticacaoException();
        }
    }

    private void validarDadosInformados(DadosJogoDTO dados) {
        if (dados == null || dados.ano() == null || eVazio(dados.titulo())
                || eVazio(dados.genero()) || eVazio(dados.desenvolvedora())) {
            throw new NegocioException(Mensagens.JOGO_DADOS_NAO_INFORMADOS);
        }
    }

    private boolean eVazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private void validarLimites(Jogo jogo) {
        if (jogo.getTitulo().length() > Mensagens.TAMANHO_MAXIMO_TITULO
                || jogo.getGenero().length() > Mensagens.TAMANHO_MAXIMO_GENERO
                || jogo.getDesenvolvedora().length() > Mensagens.TAMANHO_MAXIMO_DESENVOLVEDORA) {
            throw new NegocioException(Mensagens.JOGO_DADOS_LONGOS);
        }
        if (jogo.getAno() < Mensagens.ANO_MINIMO_JOGO
                || jogo.getAno() > Mensagens.ANO_MAXIMO_JOGO) {
            throw new NegocioException(Mensagens.JOGO_ANO_INVALIDO);
        }
    }

    private Integer converterAno(String anoTexto) {
        if (anoTexto == null || anoTexto.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(anoTexto.trim());
        } catch (NumberFormatException e) {
            throw new NegocioException(Mensagens.ANO_FILTRO_INVALIDO);
        }
    }
}
