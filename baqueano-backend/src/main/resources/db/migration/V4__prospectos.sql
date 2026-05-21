-- ============================================================
-- V4__prospectos.sql
-- Tabla de prospectos (leads / oportunidades de negocio).
-- Solo el perfil ADMIN puede ver y operar esta seccion.
-- ============================================================

CREATE TABLE prospectos (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    empresa         VARCHAR(150),
    email           VARCHAR(150) NOT NULL,
    telefono        VARCHAR(30),
    estado          ENUM('NUEVO','CONTACTADO','CALIFICADO','PERDIDO','CONVERTIDO')
                        NOT NULL DEFAULT 'NUEVO',
    origen          ENUM('REFERIDO','WEB','RED_SOCIAL','LLAMADA','EMAIL','OTRO')
                        NOT NULL DEFAULT 'WEB',
    notas           TEXT,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_creacion     VARCHAR(50),
    usuario_modificacion VARCHAR(50),
    PRIMARY KEY (id),
    KEY idx_prospectos_estado  (estado),
    KEY idx_prospectos_email   (email),
    KEY idx_prospectos_activo  (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Submenu Prospectos — agrupado en MENU PRINCIPAL, orden 6
-- ============================================================
INSERT INTO submenu (menu_id, nombre, ruta, icono, orden)
SELECT id, 'Prospectos', '/prospectos', 'target', 6
FROM   menu
WHERE  nombre = 'MENU PRINCIPAL';

-- ============================================================
-- Permisos: SOLO el perfil ADMIN (perfil_id = 1) puede operar.
-- GESTOR y CONSULTA NO tienen acceso (no se inserta ninguna fila
-- para ellos, por lo que el PermisoEvaluator devolverá false).
-- ============================================================
INSERT INTO submenu_perfil (submenu_id, perfil_id, puede_ver, puede_crear, puede_editar, puede_eliminar)
SELECT s.id, 1, TRUE, TRUE, TRUE, TRUE
FROM   submenu s
WHERE  s.ruta = '/prospectos';
