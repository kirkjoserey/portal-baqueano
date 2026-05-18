-- ============================================================
-- V2__seed.sql
-- Datos semilla iniciales.
--
-- IMPORTANTE - password admin:
-- El valor de password_hash del usuario 'admin' es un placeholder
-- intencionalmente NO compatible con BCrypt (no empieza con $2).
-- Un CommandLineRunner en el arranque detecta ese formato invalido
-- y regenera el hash de "admin123" con BCryptPasswordEncoder(10).
-- Se implementa en la Fase 5 (Spring Security + JWT).
-- ============================================================

INSERT INTO perfiles (nombre, descripcion) VALUES
 ('ADMIN','Administrador con acceso total'),
 ('GESTOR','Acceso de gestion sin administracion de usuarios'),
 ('CONSULTA','Solo lectura');

INSERT INTO usuarios (username, email, password_hash, nombre, apellido, perfil_id, activo)
VALUES ('admin', 'admin@baqueano.local',
        '!REGENERATE_ON_STARTUP!',
        'Admin', 'Sistema', 1, TRUE);

INSERT INTO parametros (clave, valor, descripcion, tipo_dato) VALUES
 ('app.nombre','Baqueano','Nombre visible de la aplicacion','STRING'),
 ('app.version','1.0.0','Version actual','STRING'),
 ('sesion.timeout.minutos','30','Minutos de inactividad antes de cerrar sesion','NUMBER'),
 ('login.intentos.max','5','Intentos maximos antes de bloquear','NUMBER');

INSERT INTO menu (nombre, icono, orden) VALUES
 ('MENU PRINCIPAL', NULL, 1);

INSERT INTO submenu (menu_id, nombre, ruta, icono, orden) VALUES
 (1, 'Dashboard',   '/dashboard',   'home',     1),
 (1, 'Parametros',  '/parametros',  'settings', 2),
 (1, 'Usuarios',    '/usuarios',    'users',    3),
 (1, 'Perfiles',    '/perfiles',    'shield',   4),
 (1, 'Contactos',   '/contactos',   'mail',     5);

-- Permisos por perfil
-- ADMIN (perfil_id=1): todo con todos los permisos
INSERT INTO submenu_perfil (submenu_id, perfil_id, puede_ver, puede_crear, puede_editar, puede_eliminar)
SELECT s.id, 1, TRUE, TRUE, TRUE, TRUE
FROM submenu s;

-- GESTOR (perfil_id=2): no ve Usuarios ni Perfiles, sin permiso de borrar
INSERT INTO submenu_perfil (submenu_id, perfil_id, puede_ver, puede_crear, puede_editar, puede_eliminar)
SELECT s.id, 2, TRUE, TRUE, TRUE, FALSE
FROM submenu s
WHERE s.ruta NOT IN ('/usuarios', '/perfiles');

-- CONSULTA (perfil_id=3): solo lectura, sin Parametros
INSERT INTO submenu_perfil (submenu_id, perfil_id, puede_ver, puede_crear, puede_editar, puede_eliminar)
SELECT s.id, 3, TRUE, FALSE, FALSE, FALSE
FROM submenu s
WHERE s.ruta NOT IN ('/parametros');
