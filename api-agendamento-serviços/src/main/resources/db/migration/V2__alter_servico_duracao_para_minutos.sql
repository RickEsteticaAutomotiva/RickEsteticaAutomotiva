ALTER TABLE servico DROP COLUMN duracao_horas;

ALTER TABLE servico ADD COLUMN duracao_minutos INT NOT NULL;