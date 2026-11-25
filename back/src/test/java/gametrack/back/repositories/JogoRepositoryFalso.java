package gametrack.back.repositories;

import gametrack.back.model.Jogo;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório de jogos para testes: a listagem é definida por cada teste, e os jogos inseridos
 * ficam guardados para conferência.
 */
public class JogoRepositoryFalso implements JogoRepository {

    private final Listagem listagem;
    private final List<Jogo> inseridos = new ArrayList<>();

    /**
     * Cria o repositório.
     *
     * @param listagem resposta da listagem
     */
    public JogoRepositoryFalso(Listagem listagem) {
        this.listagem = listagem;
    }

    /**
     * Devolve os jogos inseridos.
     *
     * @return jogos na ordem de inserção
     */
    public List<Jogo> inseridos() {
        return inseridos;
    }

    @Override
    public List<Jogo> listarComFiltros(
            String titulo, String genero, String desenvolvedora, Integer ano) {
        return listagem.listar(titulo, genero, desenvolvedora, ano);
    }

    @Override
    public void inserir(Jogo jogo) {
        inseridos.add(jogo);
    }

    /** Resposta da listagem. */
    @FunctionalInterface
    public interface Listagem {

        /**
         * Lista os jogos.
         *
         * @param titulo filtro por título
         * @param genero filtro por gênero
         * @param desenvolvedora filtro por desenvolvedora
         * @param ano filtro por ano
         * @return jogos encontrados
         */
        List<Jogo> listar(String titulo, String genero, String desenvolvedora, Integer ano);
    }
}
