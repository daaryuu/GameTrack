package gametrack.back.repositories;

import gametrack.back.model.Jogo;
import java.util.List;

/**
 * Contrato de acesso aos dados do catálogo global de jogos.
 *
 * @see RF003
 * @see RF004
 * @see RF004.1
 * @see RNF004
 */
public interface JogoRepository {

    /**
     * Consulta os jogos do catálogo aplicando os filtros informados, limitado a 500 registros.
     *
     * @param titulo filtro parcial por título (pode ser nulo)
     * @param genero filtro parcial por gênero (pode ser nulo)
     * @param desenvolvedora filtro parcial por desenvolvedora (pode ser nulo)
     * @param ano filtro exato por ano de lançamento (pode ser nulo)
     * @return lista de jogos compatíveis ordenada por título
     */
    List<Jogo> listarComFiltros(String titulo, String genero, String desenvolvedora, Integer ano);

    /**
     * Insere um jogo no catálogo.
     *
     * @param jogo jogo já validado, sem identificador
     */
    void inserir(Jogo jogo);
}