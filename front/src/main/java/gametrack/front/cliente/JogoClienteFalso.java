package gametrack.front.cliente;

import gametrack.front.dto.FiltroJogoDTO;
import gametrack.front.dto.JogoDTO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementação de {@link JogoCliente} que guarda os jogos em memória.
 * Serve para desenvolver e testar as telas sem o servidor. Os dados somem ao fechar o programa.
 */
public class JogoClienteFalso implements JogoCliente {

    private static final String MENSAGEM_NAO_ENCONTRADO = "Jogo não encontrado!";

    private final List<JogoDTO> jogos = new ArrayList<>();
    private int proximoId = 1;

    /** Cria o cliente já com alguns jogos de exemplo. */
    public JogoClienteFalso() {
        jogosDeExemplo().forEach(this::adicionar);
    }

    @Override
    public List<JogoDTO> listar(FiltroJogoDTO filtro) {
        return jogos.stream()
                .filter(jogo -> atende(jogo, filtro))
                .sorted(Comparator.comparing(JogoDTO::titulo))
                .toList();
    }

    @Override
    public JogoDTO buscarPorId(int id) {
        return jogos.get(posicaoDe(id));
    }

    @Override
    public void cadastrar(JogoDTO jogo) {
        adicionar(jogo);
    }

    @Override
    public void alterar(JogoDTO jogo) {
        jogos.set(posicaoDe(jogo.id()), jogo);
    }

    @Override
    public void excluir(int id) {
        jogos.remove(posicaoDe(id));
    }

    private void adicionar(JogoDTO jogo) {
        jogos.add(new JogoDTO(proximoId++, jogo.titulo(), jogo.genero(),
                jogo.desenvolvedora(), jogo.ano()));
    }

    private int posicaoDe(int id) {
        for (int posicao = 0; posicao < jogos.size(); posicao++) {
            if (jogos.get(posicao).id() == id) {
                return posicao;
            }
        }
        throw new ErroServidorException(MENSAGEM_NAO_ENCONTRADO);
    }

    private boolean atende(JogoDTO jogo, FiltroJogoDTO filtro) {
        return contem(jogo.titulo(), filtro.titulo())
                && contem(jogo.genero(), filtro.genero())
                && contem(jogo.desenvolvedora(), filtro.desenvolvedora())
                && contem(String.valueOf(jogo.ano()), filtro.ano());
    }

    private boolean contem(String valor, String trecho) {
        return trecho == null || trecho.isBlank()
                || valor.toLowerCase().contains(trecho.trim().toLowerCase());
    }

    private List<JogoDTO> jogosDeExemplo() {
        return List.of(
                new JogoDTO(null, "Celeste", "Plataforma", "Maddy Makes Games", 2018),
                new JogoDTO(null, "Disco Elysium", "RPG", "ZA/UM", 2019),
                new JogoDTO(null, "Hades", "Roguelike", "Supergiant Games", 2020),
                new JogoDTO(null, "Hollow Knight", "Metroidvania", "Team Cherry", 2017),
                new JogoDTO(null, "Portal 2", "Quebra-cabeça", "Valve", 2011),
                new JogoDTO(null, "Stardew Valley", "Simulação", "ConcernedApe", 2016));
    }
}
