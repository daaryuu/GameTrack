package gametrack.back.services;

import gametrack.back.config.Mensagens;
import gametrack.back.model.FiltroJogoDTO;
import gametrack.back.model.Jogo;
import gametrack.back.model.JogoDTO;
import gametrack.back.repositories.JogoRepository;
import gametrack.back.seguranca.AutenticacaoException;
import java.util.List;

/**
 * Implementa as regras de negócio para consulta e filtragem do catálogo de jogos.
 *
 * @see RF004
 * @see RF004.1
 */
public class JogoService {

    private final JogoRepository jogoRepository;

    /**
     * Constrói o serviço injetando o repositório de jogos.
     *
     * @param jogoRepository repositório de acesso ao catálogo
     */
    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    /**
     * Lista os jogos do catálogo conforme os filtros informados para um usuário autenticado.
     *
     * @param usuarioAutenticadoId identificador do usuário autenticado na sessão
     * @param filtro critérios opcionais de filtragem
     * @return lista de DTOs dos jogos encontrados
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

        if (jogos.isEmpty()) {
            throw new NegocioException(Mensagens.NENHUM_JOGO_ENCONTRADO);
        }

        return jogos.stream().map(JogoDTO::deEntidade).toList();
    }

    private void validarUsuarioAutenticado(Integer usuarioAutenticadoId) {
        if (usuarioAutenticadoId == null || usuarioAutenticadoId <= 0) {
            throw new AutenticacaoException();
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