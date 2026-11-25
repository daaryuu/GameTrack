package gametrack.back.repositories;

import gametrack.back.model.Jogo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositório de jogos para testes: a listagem é definida por cada teste, e os jogos inseridos
 * ficam guardados para conferência.
 */
public class JogoRepositoryFalso implements JogoRepository {

    private final Listagem listagem;
    private final List<Jogo> inseridos = new ArrayList<>();
    private Jogo gravado;

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

    @Override
    public Optional<Jogo> buscarPorId(int id) {
        return Optional.ofNullable(gravado).filter(jogo -> jogo.getId() == id);
    }

    @Override
    public boolean atualizar(Jogo jogo) {
        boolean existe = buscarPorId(jogo.getId()).isPresent();
        if (existe) {
            gravado = jogo;
        }
        return existe;
    }

    @Override
    public boolean excluir(int id) {
        boolean existe = buscarPorId(id).isPresent();
        if (existe) {
            gravado = null;
        }
        return existe;
    }

    /**
     * Define o único jogo que a busca por identificador encontra.
     *
     * @param jogo jogo gravado
     */
    public void definirGravado(Jogo jogo) {
        this.gravado = jogo;
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
