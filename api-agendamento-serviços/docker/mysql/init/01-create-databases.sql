-- =====================================================
-- Script executado automaticamente na inicialização
-- do container MySQL (docker-entrypoint-initdb.d)
-- Cria os bancos de homologação além do rick_dev
-- (rick_dev já é criado via MYSQL_DATABASE no compose)
-- =====================================================

CREATE DATABASE IF NOT EXISTS rick_homolog
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Concede permissões ao usuário 'rick' nos dois bancos
GRANT ALL PRIVILEGES ON rick_dev.*     TO 'rick'@'%';
GRANT ALL PRIVILEGES ON rick_homolog.* TO 'rick'@'%';

FLUSH PRIVILEGES;

