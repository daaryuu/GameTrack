package gametrack.back.repositories;

import gametrack.back.model.Usuario;
import java.util.Optional;

/**
 * Contrato de acesso aos dados de usuários no banco.
 *
 * @see RF001
 * @see RF001.1
 * @see RF002
 */
public interface UsuarioRepository {

    /**
     * Verifica se já existe um usuário cadastrado com o apelido informado.
     *
     * @param apelido apelido a ser verificado
     * @return verdadeiro se o apelido já estiver em uso
     */
    boolean existePorApelido(String apelido);

    /**
     * Busca a conta com o apelido informado.
     *
     * @param apelido apelido da conta
     * @return conta encontrada, ou vazio se o apelido não existir
     */
    Optional<Usuario> buscarPorApelido(String apelido);

    /**
     * Insere um novo usuário no banco de dados e preenche seu identificador.
     *
     * @param usuario entidade de usuário com senha já convertida em hash
     * @return usuário persistido com ID gerado
     */
    Usuario inserir(Usuario usuario);
}