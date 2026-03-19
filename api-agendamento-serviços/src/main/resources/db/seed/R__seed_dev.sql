-- =====================================================
-- R__seed_dev.sql  (Repeatable Migration — Flyway)
-- Dados fictícios para o profile DEV.
-- Este script NÃO é executado em homolog/prod:
--   spring.flyway.locations em dev inclui classpath:db/seed
--   em homolog/prod NÃO inclui esse diretório.
-- Senha de Rodrigo: rick@2024  (bcrypt pré-gerado)
-- Demais senhas:    senha123   (plaintext — apenas dev)
-- =====================================================

-- Limpa dados na ordem inversa de dependência (idempotente)
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
DELETE FROM erro_log;
DELETE FROM email;

-- ROLES
INSERT INTO role (id, nome) VALUES (1, 'ROLE_ADMIN');
INSERT INTO role (id, nome) VALUES (2, 'ROLE_GERENTE');
INSERT INTO role (id, nome) VALUES (3, 'ROLE_CLIENTE');

-- PESSOAS
INSERT INTO pessoa (id, nome, cpf, email, telefone, data_nascimento, senha) VALUES
(1, 'Rodrigo Santos',   '12345678901', 'rodrigoapolodev@gmail.com', '11987654321', '1990-05-15',
 '$2a$10$351eVELULypd9x8ong42rOgTIMdw6sitDwqIvRIFGVDHi7cXRIGL2'),
(2, 'Maria Santos',     '23456789012', 'maria.santos@email.com',   '11876543210', '1985-08-20', 'senha123'),
(3, 'Rick',             '34567890123', 'rick@email.com',           '11765432109', '1992-12-10', 'senha123'),
(4, 'Ana Costa',        '45678901234', 'ana.costa@email.com',      '11654321098', '1988-03-25', 'senha123'),
(5, 'Carlos Ferreira',  '56789012345', 'carlos.ferreira@email.com','11543210987', '1995-07-08', 'senha123');

-- PESSOA_ROLES
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (1, 1),(1, 2),(1, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (2, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (3, 2),(3, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (4, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (5, 3);

-- CATEGORIAS
INSERT INTO categoria (id, nome) VALUES (1,'Lavagem'),(2,'Polimento'),(3,'Detalhamento');

-- SERVIÇOS
INSERT INTO servico (id, nome, descricao, preco, duracao_minutos, fk_categoria) VALUES
(1, 'Lavagem Simples',       'Lavagem externa básica do veículo',              25.00, 40, 1),
(2, 'Lavagem Completa',      'Lavagem externa e interna completa',             45.00, 60, 1),
(3, 'Enceramento',           'Aplicação de cera protetora na pintura',         80.00, 70, 2),
(4, 'Lavagem + Cera',        'Lavagem completa com enceramento',              120.00, 120, 2),
(5, 'Detalhamento Completo', 'Serviço completo de detalhamento automotivo',   200.00, 60, 3);

-- VEÍCULOS
INSERT INTO veiculo (id, placa, modelo, marca, porte, cor, ano, fk_usuario) VALUES
(1, 'ABC1234', 'Civic',    'Honda',     'Médio',   'Preto',    '2020', 1),
(2, 'DEF5678', 'Corolla',  'Toyota',    'Médio',   'Branco',   '2019', 2),
(3, 'GHI9012', 'HB20',     'Hyundai',   'Pequeno', 'Prata',    '2021', 3),
(4, 'JKL3456', 'Hilux',    'Toyota',    'Grande',  'Azul',     '2022', 4),
(5, 'MNO7890', 'Onix',     'Chevrolet', 'Pequeno', 'Vermelho', '2020', 5);

-- STATUS
INSERT INTO status (id, descricao) VALUES
(1,'ANÁLISE'),(2,'AGENDA CONFIRMADA'),(3,'EM EXECUÇÃO'),(4,'CANCELADO'),(5,'CONCLUÍDO');

-- MOTIVOS
INSERT INTO motivo (id, descricao) VALUES
(1,'DESISTENCIA'),(2,'PROBLEMA_TECNICO'),(3,'REAGENDAMENTO');

-- ORDENS DE SERVIÇO
INSERT INTO ordem_servico (data_agendamento, preco_minimo, observacoes, dt_conclusao, fk_veiculo, fk_status, fk_motivo) VALUES
('2024-01-18 10:00:00', 150.00, 'Serviço executado conforme solicitado', '2024-01-20 15:00:00', 1, 1, NULL),
('2024-01-19 09:30:00', 200.00, 'Aguardando peças',                      NULL,                  2, 3, 1),
('2024-01-20 08:00:00', 180.00, 'Cliente muito satisfeito',              '2024-01-22 17:15:00', 3, 1, NULL),
('2024-01-21 11:45:00', 120.00, 'Iniciado hoje',                         NULL,                  4, 2, NULL),
('2025-10-04 09:20:00', 100.00, 'Lavagem completa realizada',            '2025-10-04 12:15:00', 2, 1, NULL);



