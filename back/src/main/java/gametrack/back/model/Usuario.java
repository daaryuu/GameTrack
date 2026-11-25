package gametrack.back.model;

/**
 * Representa a entidade de conta de usuário no domínio do GameTrack.
 *
 * @see RF001
 */
public class Usuario {

    private Integer id;
    private String apelido;
    private String senhaHash;
    private Perfil perfil = Perfil.USUARIO;

    /**
     * Constrói um novo usuário com apelido e hash da senha.
     *
     * @param id identificador único no banco (nulo antes da gravação)
     * @param apelido nome único escolhido pelo usuário
     * @param senhaHash hash bcrypt da senha
     */
    public Usuario(Integer id, String apelido, String senhaHash) {
        this.id = id;
        this.apelido = apelido;
        this.senhaHash = senhaHash;
    }

    /**
     * Retorna o perfil da conta, que é {@link Perfil#USUARIO} se não for definido outro.
     *
     * @return perfil da conta
     */
    public Perfil getPerfil() {
        return perfil;
    }

    /**
     * Define o perfil da conta.
     *
     * @param perfil novo perfil
     */
    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    /**
     * Retorna o identificador do usuário.
     *
     * @return identificador numérico ou nulo
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define o identificador gerado após a persistência.
     *
     * @param id identificador único do usuário
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retorna o apelido único do usuário.
     *
     * @return apelido cadastrado
     */
    public String getApelido() {
        return apelido;
    }

    /**
     * Retorna o hash irreversível da senha do usuário.
     *
     * @return hash bcrypt da senha
     */
    public String getSenhaHash() {
        return senhaHash;
    }
}