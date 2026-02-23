-- =====================================================
-- reset-it.sql — limpa todas as tabelas antes dos testes
-- Executado antes de cada classe de teste IT
-- =====================================================

SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM item_servico;
DELETE FROM ordem_servico;
DELETE FROM carrinho;
DELETE FROM favorito;
DELETE FROM veiculo;
DELETE FROM servico;
DELETE FROM categoria;
DELETE FROM motivo;
DELETE FROM status;
DELETE FROM pessoa_roles;
DELETE FROM pessoa;
DELETE FROM role;

-- Reseta sequences/auto-increment
ALTER TABLE pessoa ALTER COLUMN id RESTART WITH 1;
ALTER TABLE veiculo ALTER COLUMN id RESTART WITH 1;
ALTER TABLE categoria ALTER COLUMN id RESTART WITH 1;
ALTER TABLE servico ALTER COLUMN id RESTART WITH 1;
ALTER TABLE status ALTER COLUMN id RESTART WITH 1;
ALTER TABLE motivo ALTER COLUMN id RESTART WITH 1;
ALTER TABLE ordem_servico ALTER COLUMN id RESTART WITH 1;
ALTER TABLE item_servico ALTER COLUMN id RESTART WITH 1;
ALTER TABLE carrinho ALTER COLUMN id RESTART WITH 1;
ALTER TABLE favorito ALTER COLUMN id RESTART WITH 1;
ALTER TABLE role ALTER COLUMN id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;

