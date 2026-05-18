-- ============================================================
-- V3__refresh_tokens.sql
-- Almacenamiento de refresh tokens (decision C-1 de Fase 0).
-- El campo 'token' guarda el hash SHA-256 del token real, no el
-- token en claro: el cliente recibe el secreto y solo conservamos
-- el digest para validacion/revocacion.
-- ============================================================

CREATE TABLE refresh_tokens (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id      BIGINT       NOT NULL,
    token           VARCHAR(255) NOT NULL,        -- SHA-256 hex del refresh token
    expira_en       DATETIME     NOT NULL,
    revocado        BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_revocacion DATETIME    NULL,
    ip_origen       VARCHAR(45)  NULL,
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_token (token),
    KEY idx_refresh_tokens_usuario (usuario_id),
    KEY idx_refresh_tokens_expira (expira_en),
    CONSTRAINT fk_refresh_tokens_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
