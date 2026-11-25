-- ==============================================================
-- GameTrack: criação do banco e das tabelas (MySQL 9.7 LTS)
-- Segue o Dicionário de Dados do documento de requisitos.
-- Uso: mysql -u <usuario> -p < sql/01_criar_tabelas.sql
-- Pode ser executado de novo: não apaga nem altera tabelas existentes.
-- ==============================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS gametrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gametrack;

-- Contas do sistema (RF001, RF002, RNF005, RNF006)
CREATE TABLE IF NOT EXISTS Usuario (
    id         INT         NOT NULL AUTO_INCREMENT,
    apelido    VARCHAR(30) NOT NULL,
    senha_hash CHAR(60)    NOT NULL,
    perfil     ENUM('USUARIO', 'ADMINISTRADOR') NOT NULL DEFAULT 'USUARIO',
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_apelido UNIQUE (apelido),
    CONSTRAINT ck_usuario_apelido CHECK (CHAR_LENGTH(TRIM(apelido)) >= 3)
) ENGINE = InnoDB;

-- Catálogo global de jogos (RF003 a RF006)
CREATE TABLE IF NOT EXISTS Jogo (
    id             INT          NOT NULL AUTO_INCREMENT,
    titulo         VARCHAR(100) NOT NULL,
    genero         VARCHAR(40)  NOT NULL,
    desenvolvedora VARCHAR(80)  NOT NULL,
    ano            SMALLINT     NOT NULL,
    CONSTRAINT pk_jogo PRIMARY KEY (id),
    CONSTRAINT ck_jogo_titulo CHECK (TRIM(titulo) <> ''),
    CONSTRAINT ck_jogo_genero CHECK (TRIM(genero) <> ''),
    CONSTRAINT ck_jogo_desenvolvedora CHECK (TRIM(desenvolvedora) <> ''),
    CONSTRAINT ck_jogo_ano CHECK (ano BETWEEN 1950 AND 2100),
    -- Ordenação da listagem do catálogo (RNF004)
    INDEX ix_jogo_titulo (titulo)
) ENGINE = InnoDB;

-- Categorias personalizadas de cada usuário (RF011)
CREATE TABLE IF NOT EXISTS Categoria (
    id         INT         NOT NULL AUTO_INCREMENT,
    usuario_id INT         NOT NULL,
    nome       VARCHAR(40) NOT NULL,
    CONSTRAINT pk_categoria PRIMARY KEY (id),
    CONSTRAINT uk_categoria_usuario_nome UNIQUE (usuario_id, nome),
    CONSTRAINT fk_categoria_usuario FOREIGN KEY (usuario_id)
        REFERENCES Usuario (id) ON DELETE RESTRICT,
    CONSTRAINT ck_categoria_nome CHECK (TRIM(nome) <> '')
) ENGINE = InnoDB;

-- Itens da biblioteca de cada usuário (RF007 a RF010, RF012)
-- Ficam a cargo do service: a categoria ser do mesmo usuário (RF011.1) e a review só ser
-- escrita com status CONCLUIDO (RF012.1). Um CHECK para a review impediria mudar
-- o status depois, por exemplo de CONCLUIDO para PLATINADO.
CREATE TABLE IF NOT EXISTS ItemBiblioteca (
    id               INT          NOT NULL AUTO_INCREMENT,
    usuario_id       INT          NOT NULL,
    jogo_id          INT          NOT NULL,
    plataforma       ENUM('PC', 'NINTENDO', 'PLAYSTATION') NOT NULL,
    status           ENUM('BACKLOG', 'SENDO_JOGADO', 'CONCLUIDO', 'ABANDONADO', 'PLATINADO')
                     NOT NULL DEFAULT 'BACKLOG',
    tempo_jogado_min INT UNSIGNED NOT NULL DEFAULT 0,
    nota             TINYINT      NULL,
    review           TEXT         NULL,
    categoria_id     INT          NULL,
    CONSTRAINT pk_item_biblioteca PRIMARY KEY (id),
    CONSTRAINT fk_item_usuario FOREIGN KEY (usuario_id)
        REFERENCES Usuario (id) ON DELETE RESTRICT,
    -- Impede excluir jogo que está em alguma biblioteca (RF006.1)
    CONSTRAINT fk_item_jogo FOREIGN KEY (jogo_id)
        REFERENCES Jogo (id) ON DELETE RESTRICT,
    CONSTRAINT fk_item_categoria FOREIGN KEY (categoria_id)
        REFERENCES Categoria (id) ON DELETE SET NULL,
    CONSTRAINT ck_item_nota CHECK (nota BETWEEN 1 AND 5),
    -- Toda review exige nota (RF012.2)
    CONSTRAINT ck_item_review_nota CHECK (review IS NULL OR nota IS NOT NULL)
) ENGINE = InnoDB;
