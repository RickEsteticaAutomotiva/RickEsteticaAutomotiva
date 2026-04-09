-- Migra a duracao de servico de TIME para minutos inteiros
ALTER TABLE servico ADD COLUMN duracao_minutos INT;

UPDATE servico
SET duracao_minutos = EXTRACT(HOUR FROM duracao_horas) * 60 + EXTRACT(MINUTE FROM duracao_horas)
WHERE duracao_horas IS NOT NULL;

ALTER TABLE servico DROP COLUMN duracao_horas;

