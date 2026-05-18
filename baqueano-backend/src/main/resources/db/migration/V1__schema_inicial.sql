-- ============================================================
-- V1__schema_inicial.sql
-- Schema inicial del Portal Baqueano.
-- Convenciones:
--   * InnoDB / utf8mb4 / utf8mb4_unicode_ci
--   * Todas las tablas llevan columnas de auditoria
--   * FK con ON DELETE RESTRICT salvo que se indique lo contrario
-- ============================================================

CREATE TABLE perfiles (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(50)  NOT NULL,
    descripcion     VARCHAR(255),
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_perfiles_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    username        VARCHAR(50)  NOT NULL,
    email           VARCHAR(150) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    perfil_id       BIGINT       NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    ultimo_login    DATETIME     NULL,
    intentos_fallidos INT        NOT NULL DEFAULT 0,
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_username (username),
    UNIQUE KEY uk_usuarios_email (email),
    CONSTRAINT fk_usuarios_perfil FOREIGN KEY (perfil_id) REFERENCES perfiles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE parametros (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    clave           VARCHAR(100) NOT NULL,
    valor           VARCHAR(500) NOT NULL,
    descripcion     VARCHAR(255),
    tipo_dato       ENUM('STRING','NUMBER','BOOLEAN','JSON','DATE') NOT NULL DEFAULT 'STRING',
    editable        BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_parametros_clave (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE contactos (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    telefono        VARCHAR(30),
    asunto          VARCHAR(150),
    mensaje         TEXT         NOT NULL,
    estado          ENUM('NUEVO','LEIDO','RESPONDIDO','ARCHIVADO') NOT NULL DEFAULT 'NUEVO',
    ip_origen       VARCHAR(45),
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    KEY idx_contactos_estado (estado),
    KEY idx_contactos_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Menu dinamico (jerarquico, con permisos por perfil)
-- ============================================================

CREATE TABLE menu (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(80)  NOT NULL,
    icono           VARCHAR(50),
    orden           INT          NOT NULL DEFAULT 0,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_menu_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE submenu (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    menu_id         BIGINT       NOT NULL,
    nombre          VARCHAR(80)  NOT NULL,
    ruta            VARCHAR(200) NOT NULL,
    icono           VARCHAR(50),
    orden           INT          NOT NULL DEFAULT 0,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_submenu_ruta (ruta),
    CONSTRAINT fk_submenu_menu FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE submenu_perfil (
    submenu_id      BIGINT       NOT NULL,
    perfil_id       BIGINT       NOT NULL,
    puede_ver       BOOLEAN      NOT NULL DEFAULT TRUE,
    puede_crear     BOOLEAN      NOT NULL DEFAULT FALSE,
    puede_editar    BOOLEAN      NOT NULL DEFAULT FALSE,
    puede_eliminar  BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (submenu_id, perfil_id),
    CONSTRAINT fk_sp_submenu FOREIGN KEY (submenu_id) REFERENCES submenu(id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_perfil  FOREIGN KEY (perfil_id)  REFERENCES perfiles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
