package gametrack.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static final Jogo HOLLOW_KNIGHT =
            new Jogo(1, "Hollow Knight", "Metroidvania", "Team Cherry", 2017);

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
        JogoService service = new JogoService((t, g, d, a) -> List.of(HOLLOW_KNIGHT));

        List<JogoDTO> resultado = service.listarCatalogo(
                10, new FiltroJogoDTO("Hollow", null, null, "2017"));

        assertEquals(1, resultado.size());
        assertEquals("Hollow Knight", resultado.get(0).titulo());
    }

    @Test
    void deveRetornarListaVaziaQuandoNenhumJogoForEncontrado() {
        JogoService service = new JogoService((t, g, d, a) -> List.of());

        List<JogoDTO> resultado = service.listarCatalogo(
                10, new FiltroJogoDTO("Inexistente", null, null, null));

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRejeitarAnoNaoNumericoNoFiltro() {
        JogoService service = new JogoService((t, g, d, a) -> List.of());

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> service.listarCatalogo(10, new FiltroJogoDTO(null, null, null, "abc")));

        assertEquals(Mensagens.ANO_FILTRO_INVALIDO, excecao.getMessage());
    }

    @Test
    void deveTratarFiltroNuloComoListagemCompleta() {
        Object[] recebido = new Object[4];
        JogoRepository repositorio = (titulo, genero, desenvolvedora, ano) -> {
            recebido[0] = titulo;
            recebido[3] = ano;
            return List.of(HOLLOW_KNIGHT);
        };

        List<JogoDTO> resultado = new JogoService(repositorio).listarCatalogo(10, null);

        assertEquals(1, resultado.size());
        assertNull(recebido[0]);
        assertNull(recebido[3]);
    }

    @Test
    void deveConverterAnoDoFiltroParaNumero() {
        Object[] recebido = new Object[1];
        JogoRepository repositorio = (titulo, genero, desenvolvedora, ano) -> {
            recebido[0] = ano;
            return List.of();
        };

        new JogoService(repositorio).listarCatalogo(
                10, new FiltroJogoDTO(null, null, null, " 2011 "));

        assertEquals(2011, recebido[0]);
    }
}
