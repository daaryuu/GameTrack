package gametrack.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gametrack.back.config.Mensagens;
import gametrack.back.model.FiltroJogoDTO;
import gametrack.back.model.Jogo;
import gametrack.back.model.JogoDTO;
import gametrack.back.repositories.JogoRepository;
import gametrack.back.seguranca.AutenticacaoException;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários das regras de negócio de listagem e filtragem do catálogo.
 *
 * @see RF004
 * @see RF004.1
 */
class JogoServiceTest {

    @Test
    void deveExigirUsuarioAutenticado() {
        JogoService service = new JogoService((t, g, d, a) -> List.of());

        AutenticacaoException excecao = assertThrows(
                AutenticacaoException.class,
                () -> service.listarCatalogo(null, FiltroJogoDTO.semFiltro()));

        assertEquals(Mensagens.USUARIO_NAO_AUTENTICADO, excecao.getMessage());
    }

    @Test
    void deveListarJogosFiltradosQuandoEncontrados() {
        JogoRepository repoFake = (titulo, genero, dev, ano) -> List.of(
                new Jogo(1, "Hollow Knight", "Metroidvania", "Team Cherry", 2017));
        JogoService service = new JogoService(repoFake);

        List<JogoDTO> resultado = service.listarCatalogo(
                10, new FiltroJogoDTO("Hollow", null, null, "2017"));

        assertEquals(1, resultado.size());
        assertEquals("Hollow Knight", resultado.get(0).titulo());
    }

    @Test
    void deveLancarExcecaoQuandoNenhumJogoForEncontrado() {
        JogoService service = new JogoService((t, g, d, a) -> List.of());

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> service.listarCatalogo(10, new FiltroJogoDTO("Inexistente", null, null, null)));

        assertEquals(Mensagens.NENHUM_JOGO_ENCONTRADO, excecao.getMessage());
    }

    @Test
    void deveRejeitarAnoNaoNumericoNoFiltro() {
        JogoService service = new JogoService((t, g, d, a) -> List.of());

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> service.listarCatalogo(10, new FiltroJogoDTO(null, null, null, "abc")));

        assertEquals(Mensagens.ANO_FILTRO_INVALIDO, excecao.getMessage());
    }
}