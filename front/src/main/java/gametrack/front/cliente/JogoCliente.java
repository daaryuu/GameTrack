package gametrack.front.cliente;

import java.util.List;
import gametrack.front.dto.FiltroJogoDTO;
import gametrack.front.dto.JogoDTO;

public interface JogoCliente {

    List<JogoDTO> listar(FiltroJogoDTO filtro);

    JogoDTO buscarPorId(int id);

    void cadastrar(JogoDTO jogo);

    void alterar(JogoDTO jogo);

    void excluir(int id);
}
