-- =====================================================
-- V2__create_table_calendario_evento.sql
-- Tabela para rastrear eventos sincronizados com o Google Calendar
-- Compatível com MySQL 8+ e H2 (profiles dev/test)
-- =====================================================

CREATE TABLE IF NOT EXISTS calendario_evento (
    id                BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fk_ordem_servico  BIGINT NOT NULL,
    evento_id_google  VARCHAR(255) NOT NULL,
    titulo            VARCHAR(255) NOT NULL,
    descricao         LONGTEXT,
    localizacao       VARCHAR(500),
    data_hora_inicio  TIMESTAMP NOT NULL,
    data_hora_fim     TIMESTAMP NOT NULL,
    fuso_horario      VARCHAR(50),
    placa_veiculo     VARCHAR(20),
    data_criacao      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_calendario_evento_google UNIQUE (evento_id_google),
    INDEX idx_calendario_evento_ordem_servico (fk_ordem_servico),
    CONSTRAINT fk_calendario_evento_ordem_servico FOREIGN KEY (fk_ordem_servico) REFERENCES ordem_servico (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Eventos agendados no Google Calendar vinculados às ordens de serviço';

