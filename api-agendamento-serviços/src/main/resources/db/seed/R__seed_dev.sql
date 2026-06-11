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
(4, 'Ana Costa',        '45678900123', 'ana.costa@email.com',      '11654321098', '1988-03-25', 'senha123'),
(5, 'Carlos Ferreira',  '56789012345', 'carlos.ferreira@email.com','11543210987', '1995-07-08', 'senha123');

-- PESSOA_ROLES
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (1, 1),(1, 2),(1, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (2, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (3, 2),(3, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (4, 3);
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (5, 3);

-- CATEGORIAS (mesmas da V3 + V6)
INSERT INTO categoria (id, nome) VALUES
    (1, 'Lavagem'),
    (2, 'Polimento'),
    (3, 'Detalhamento'),
    (4, 'Vitrificação'),
    (5, 'Higienização');

-- SERVIÇOS (catálogo definitivo — igual V6)
INSERT INTO servico (id, nome, descricao, preco, duracao_minutos, imagem, fk_categoria) VALUES
    (1,  'Lavagem Técnica Carro P',          'Lavagem detalhada para veículos pequenos',              70.00,   45, 'Lavagem_Tecnica_Carro_P',          1),
    (2,  'Lavagem Técnica Carro M',          'Lavagem detalhada para veículos médios',                80.00,   60, 'Lavagem_Tecnica_Carro_M',          1),
    (3,  'Lavagem Técnica Carro G',          'Lavagem detalhada para veículos grandes',               90.00,   75, 'Lavagem_Tecnica_Carro_G',          1),
    (4,  'Lavagem Premium',                  'Descontaminação ferrosa, rodas e cera líquida',        100.00,   90, 'Lavagem_Premium',                  1),
    (5,  'Limpeza Técnica de Motor',         'Limpeza detalhada com proteção do motor',              120.00,   60, 'Limpeza_Tecnica_De_Motor',         1),
    (6,  'Limpeza Técnica de Chassi',        'Limpeza profunda da parte inferior',                   400.00,  120, 'Limpeza_Tecnica_De_Chassi',        1),
    (7,  'Enceramento Técnico',              'Aplicação profissional de cera de alta performance',   200.00,   90, 'Enceramento_tecnico',              2),
    (8,  'Polimento de Farol',               'Restauração e proteção UV de faróis',                  200.00,   60, 'Polimento_de_Farol',               2),
    (9,  'Polimento Técnico',                'Correção de pintura e brilho intenso',                 800.00,  240, 'Polimento_Tecnica',                2),
    (10, 'Cristalização de Pintura',         'Proteção e espelhamento da lataria',                   250.00,  120, 'Cristalizacao_de_Pintura',         2),
    (11, 'Vitrificação de Pintura',          'Proteção cerâmica de longa duração (lataria)',        1250.00,  360, 'Vitrificacao_de_Pintura',          4),
    (12, 'Vitrificação de Plásticos',        'Proteção cerâmica para plásticos externos',            350.00,   90, NULL,                               4),
    (13, 'Vitrificação de Couro',            'Proteção cerâmica para o interior em couro',           300.00,  120, 'Vitrificacao_de_Couro',            4),
    (14, 'Vitrificação de Pára-brisa',       'Tratamento hidrofóbico para vidros',                   150.00,   45, NULL,                               4),
    (15, 'Remoção de Chuva Ácida Vidros',    'Remoção de manchas de água nos vidros',                100.00,   60, NULL,                               2),
    (16, 'Higienização Interna Completa',    'Limpeza profunda de todo o habitáculo',                800.00,  300, 'Higienizacao_Interna_Completa',    5),
    (17, 'Higienização Teto e Colunas',      'Limpeza específica do forro e colunas',                150.00,   90, 'Higienizacao_Teto_e_Colunas',      5),
    (18, 'Higienização Bancos Couro/Tecido', 'Limpeza e hidratação de assentos',                     180.00,  120, NULL,                               5),
    (19, 'Oxi Sanitização',                  'Eliminação de odores e bactérias com Ozônio',          120.00,   40, NULL,                               5),
    (20, 'Revitalização de Plásticos Internos','Recuperação do brilho original interno',               50.00,   30, 'Revitalizacao_de_Plasticos_Internos', 3);

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
