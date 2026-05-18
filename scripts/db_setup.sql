-- ============================================================
-- Script administrativo Baqueano (ejecutar como root).
-- NO forma parte de Flyway: solo crea la BD y el usuario de
-- aplicacion. Las migraciones de schema viven en
-- baqueano-backend/src/main/resources/db/migration.
-- Idempotente: se puede correr varias veces sin error.
-- ============================================================

CREATE DATABASE IF NOT EXISTS baqueano
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- BD aislada para tests automatizados (Fase 3+)
CREATE DATABASE IF NOT EXISTS baqueano_test
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'baqueano'@'localhost' IDENTIFIED BY 'baqueano';
CREATE USER IF NOT EXISTS 'baqueano'@'127.0.0.1' IDENTIFIED BY 'baqueano';

GRANT ALL PRIVILEGES ON baqueano.*      TO 'baqueano'@'localhost';
GRANT ALL PRIVILEGES ON baqueano.*      TO 'baqueano'@'127.0.0.1';
GRANT ALL PRIVILEGES ON baqueano_test.* TO 'baqueano'@'localhost';
GRANT ALL PRIVILEGES ON baqueano_test.* TO 'baqueano'@'127.0.0.1';

FLUSH PRIVILEGES;
