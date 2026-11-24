package gametrack.back.repositories;

import gametrack.back.model.Usuario;

/**
 * Contrato de acesso aos dados de usuários no banco.
 *
 * @see RF001
 * @see RF001.1
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
     * Insere um novo usuário no banco de dados e preenche seu identificador.
     *
     * @param usuario entidade de usuário com senha já convertida em hash
     * @return usuário persistido com ID gerado
     */
    Usuario inserir(Usuario usuario);
}