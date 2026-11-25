package gametrack.back.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gametrack.back.config.Mensagens;
import gametrack.back.model.DadosJogoDTO;
import gametrack.back.model.FiltroJogoDTO;
import gametrack.back.model.Jogo;
import gametrack.back.model.JogoDTO;
import gametrack.back.model.Perfil;
import gametrack.back.repositories.JogoRepositoryFalso;
import gametrack.back.seguranca.AutenticacaoException;
import gametrack.back.seguranca.PermissaoException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários das regras de listagem, filtragem, cadastro e alteração do catálogo.
 *
 * @see RF003
 * @see RF004
 * @see RF004.1
 * @see RF005
 * @see RF006
 */
class JogoServiceTest {

    private static final Jogo HOLLOW_KNIGHT =
            new Jogo(1, "Hollow Knight", "Metroidvania", "Team Cherry", 2017);

    private static final int ADMINISTRADOR_ID = 1;
    private static final Map<Integer, Perfil> PERFIS =
            Map.of(ADMINISTRADOR_ID, Perfil.ADMINISTRADOR, 2, Perfil.USUARIO);

    private final JogoRepositoryFalso repositorioCadastro =
            new JogoRepositoryFalso((t, g, d, a) -> List.of());
    private final JogoService cadastro = new JogoService(
            repositorioCadastro, id -> Optional.ofNullable(PERFIS.get(id)));

    @Test
    void deveExigirUsuarioAutenticado() {
        JogoService service = criarService((t, g, d, a) -> List.of());

        AutenticacaoException excecao = assertThrows(
                AutenticacaoException.class,
                () -> service.listarCatalogo(null, FiltroJogoDTO.semFiltro()));

        assertEquals(Mensagens.USUARIO_NAO_AUTENTICADO, excecao.getMessage());
    }

    @Test
    void deveListarJogosFiltradosQuandoEncontrados() {
        JogoService service = criarService((t, g, d, a) -> List.of(HOLLOW_KNIGHT));

        List<JogoDTO> resultado = service.listarCatalogo(
                10, new FiltroJogoDTO("Hollow", null, null, "2017"));

        assertEquals(1, resultado.size());
        assertEquals("Hollow Knight", resultado.get(0).titulo());
    }

    @Test
    void deveRetornarListaVaziaQuandoNenhumJogoForEncontrado() {
        JogoService service = criarService((t, g, d, a) -> List.of());

        List<JogoDTO> resultado = service.listarCatalogo(
                10, new FiltroJogoDTO("Inexistente", null, null, null));

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRejeitarAnoNaoNumericoNoFiltro() {
        JogoService service = criarService((t, g, d, a) -> List.of());

        NegocioException excecao = assertThrows(
                NegocioException.class,
                () -> service.listarCatalogo(10, new FiltroJogoDTO(null, null, null, "abc")));

        assertEquals(Mensagens.ANO_FILTRO_INVALIDO, excecao.getMessage());
    }

    @Test
    void deveTratarFiltroNuloComoListagemCompleta() {
        Object[] recebido = new Object[4];
        JogoRepositoryFalso.Listagem repositorio = (titulo, genero, desenvolvedora, ano) -> {
            recebido[0] = titulo;
            recebido[3] = ano;
            return List.of(HOLLOW_KNIGHT);
        };

        List<JogoDTO> resultado = criarService(repositorio).listarCatalogo(10, null);

        assertEquals(1, resultado.size());
        assertNull(recebido[0]);
        assertNull(recebido[3]);
    }

    @Test
    void deveConverterAnoDoFiltroParaNumero() {
        Object[] recebido = new Object[1];
        JogoRepositoryFalso.Listagem repositorio = (titulo, genero, desenvolvedora, ano) -> {
            recebido[0] = ano;
            return List.of();
        };

        criarService(repositorio).listarCatalogo(
                10, new FiltroJogoDTO(null, null, null, " 2011 "));

        assertEquals(2011, recebido[0]);
    }

    @Test
    void deveCadastrarJogoSemEspacosNasPontas() {
        cadastro.cadastrar(ADMINISTRADOR_ID,
                new DadosJogoDTO(" Celeste ", " Plataforma ", " Maddy Makes Games ", 2018));

        Jogo inserido = repositorioCadastro.inseridos().get(0);
        assertEquals("Celeste", inserido.getTitulo());
        assertEquals("Plataforma", inserido.getGenero());
        assertEquals("Maddy Makes Games", inserido.getDesenvolvedora());
        assertEquals(2018, inserido.getAno());
    }

    @Test
    void deveImpedirCadastroPorQuemNaoEAdministrador() {
        assertThrows(PermissaoException.class, () -> cadastro.cadastrar(2, jogo(2018)));
        assertThrows(PermissaoException.class, () -> cadastro.cadastrar(99, jogo(2018)));
        assertThrows(AutenticacaoException.class, () -> cadastro.cadastrar(null, jogo(2018)));
        assertTrue(repositorioCadastro.inseridos().isEmpty());
    }

    @Test
    void deveExigirTodosOsDadosDoJogo() {
        conferirRecusa(null, Mensagens.JOGO_DADOS_NAO_INFORMADOS);
        conferirRecusa(jogo(null), Mensagens.JOGO_DADOS_NAO_INFORMADOS);
        for (String vazio : new String[] {null, "", "  "}) {
            conferirRecusa(new DadosJogoDTO(vazio, "G", "D", 2018),
                    Mensagens.JOGO_DADOS_NAO_INFORMADOS);
            conferirRecusa(new DadosJogoDTO("T", vazio, "D", 2018),
                    Mensagens.JOGO_DADOS_NAO_INFORMADOS);
            conferirRecusa(new DadosJogoDTO("T", "G", vazio, 2018),
                    Mensagens.JOGO_DADOS_NAO_INFORMADOS);
        }
    }

    @Test
    void deveRespeitarOsLimitesDoDicionarioDeDados() {
        conferirRecusa(jogo(1949), Mensagens.JOGO_ANO_INVALIDO);
        conferirRecusa(jogo(2101), Mensagens.JOGO_ANO_INVALIDO);
        conferirRecusa(new DadosJogoDTO("t".repeat(101), "G", "D", 2018),
                Mensagens.JOGO_DADOS_LONGOS);
        conferirRecusa(new DadosJogoDTO("T", "g".repeat(41), "D", 2018),
                Mensagens.JOGO_DADOS_LONGOS);
        conferirRecusa(new DadosJogoDTO("T", "G", "d".repeat(81), 2018),
                Mensagens.JOGO_DADOS_LONGOS);

        cadastro.cadastrar(ADMINISTRADOR_ID, jogo(1950));
        cadastro.cadastrar(ADMINISTRADOR_ID, new DadosJogoDTO(
                "t".repeat(100), "g".repeat(40), "d".repeat(80), 2100));
        assertEquals(2, repositorioCadastro.inseridos().size());
    }

    @Test
    void deveBuscarJogoPorId() {
        repositorioCadastro.definirGravado(HOLLOW_KNIGHT);

        assertEquals("Hollow Knight", cadastro.buscarPorId(2, 1).titulo());
        assertThrows(NaoEncontradoException.class, () -> cadastro.buscarPorId(2, 99));
        assertThrows(AutenticacaoException.class, () -> cadastro.buscarPorId(null, 1));
    }

    @Test
    void deveAlterarJogoMantendoOId() {
        repositorioCadastro.definirGravado(HOLLOW_KNIGHT);

        cadastro.alterar(ADMINISTRADOR_ID, 1, new DadosJogoDTO(" Novo ", "RPG", "Estúdio", 2020));

        Jogo alterado = repositorioCadastro.buscarPorId(1).orElseThrow();
        assertEquals("Novo", alterado.getTitulo());
        assertEquals("RPG", alterado.getGenero());
        assertEquals("Estúdio", alterado.getDesenvolvedora());
        assertEquals(2020, alterado.getAno());
    }

    @Test
    void deveRecusarAlteracaoSemPermissaoInvalidaOuDeJogoInexistente() {
        repositorioCadastro.definirGravado(HOLLOW_KNIGHT);

        assertThrows(PermissaoException.class, () -> cadastro.alterar(2, 1, jogo(2018)));
        assertThrows(AutenticacaoException.class, () -> cadastro.alterar(null, 1, jogo(2018)));
        assertThrows(NegocioException.class,
                () -> cadastro.alterar(ADMINISTRADOR_ID, 1, jogo(null)));
        assertThrows(NegocioException.class,
                () -> cadastro.alterar(ADMINISTRADOR_ID, 1, jogo(1949)));
        assertThrows(NaoEncontradoException.class,
                () -> cadastro.alterar(ADMINISTRADOR_ID, 99, jogo(2018)));
        assertEquals("Hollow Knight",
                repositorioCadastro.buscarPorId(1).orElseThrow().getTitulo());
    }

    @Test
    void deveExcluirJogoSoPeloAdministrador() {
        repositorioCadastro.definirGravado(HOLLOW_KNIGHT);

        assertThrows(PermissaoException.class, () -> cadastro.excluir(2, 1));
        assertThrows(AutenticacaoException.class, () -> cadastro.excluir(null, 1));
        assertTrue(repositorioCadastro.buscarPorId(1).isPresent());

        cadastro.excluir(ADMINISTRADOR_ID, 1);
        assertTrue(repositorioCadastro.buscarPorId(1).isEmpty());
        assertThrows(NaoEncontradoException.class, () -> cadastro.excluir(ADMINISTRADOR_ID, 1));
    }

    private void conferirRecusa(DadosJogoDTO dados, String mensagem) {
        NegocioException erro = assertThrows(NegocioException.class,
                () -> cadastro.cadastrar(ADMINISTRADOR_ID, dados));
        assertEquals(mensagem, erro.getMessage());
    }

    private static DadosJogoDTO jogo(Integer ano) {
        return new DadosJogoDTO("T", "G", "D", ano);
    }

    private static JogoService criarService(JogoRepositoryFalso.Listagem listagem) {
        return new JogoService(new JogoRepositoryFalso(listagem), id -> Optional.empty());
    }
}
