-- =====================================================
-- seed-it.sql — dados mínimos para os testes de integração
-- Senhas: placeholder '{noop}placeholder' — serão sobrescritas pelo AbstractIntegrationTest
--         via PasswordEncoder.encode() antes de cada teste, garantindo hash correto.
-- =====================================================

-- PESSOAS (sem coluna role — roles gerenciadas em pessoa_roles via tabela role)
-- Admin: rodrigoapolodev@gmail.com / rick@2024
-- User:  maria.santos@email.com    / senha123
INSERT INTO pessoa (id, nome, cpf, email, telefone, data_nascimento, senha) VALUES
(1, 'Rodrigo Santos', '12345678901', 'rodrigoapolodev@gmail.com', '11987654321', '1990-05-15',
 '{noop}placeholder'),
(2, 'Maria Santos', '23456789012', 'maria.santos@email.com', '11876543210', '1985-08-20',
 '{noop}placeholder');

-- ROLES (entidades de referência — devem existir antes de pessoa_roles)
INSERT INTO role (id, nome) VALUES (1, 'ROLE_ADMIN');
INSERT INTO role (id, nome) VALUES (2, 'ROLE_GERENTE');
INSERT INTO role (id, nome) VALUES (3, 'ROLE_CLIENTE');

-- PESSOA_ROLES (junção N:N)
-- Rodrigo (id=1): ADMIN
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (1, 1);
-- Maria (id=2): USER
INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (2, 3);

-- CATEGORIAS
INSERT INTO categoria (id, nome) VALUES
(1, 'Lavagem'),
(2, 'Polimento'),
(3, 'Detalhamento');

-- SERVIÇOS
INSERT INTO servico (id, nome, descricao, preco, fk_categoria) VALUES
(1, 'Lavagem Simples',       'Lavagem externa básica do veículo',                 25.00, 1),
(2, 'Lavagem Completa',      'Lavagem externa e interna completa',                45.00, 1),
(3, 'Enceramento',           'Aplicação de cera protetora na pintura',            80.00, 2),
(4, 'Lavagem + Cera',        'Lavagem completa com enceramento',                 120.00, 2),
(5, 'Detalhamento Completo', 'Serviço completo de detalhamento automotivo',      200.00, 3);

-- STATUS
INSERT INTO status (id, descricao) VALUES
(1, 'ANÁLISE'),
(2, 'AGENDA CONFIRMADA'),
(3, 'EM EXECUÇÃO'),
(4, 'CANCELADO'),
(5, 'CONCLUÍDO');

-- MOTIVOS DE CANCELAMENTO
INSERT INTO motivo (id, descricao) VALUES
(1, 'DESISTENCIA'),
(2, 'PROBLEMA_TECNICO'),
(3, 'REAGENDAMENTO');

-- VEÍCULOS (pessoa_id = 1 → admin; pessoa_id = 2 → user)
INSERT INTO veiculo (id, placa, modelo, marca, porte, cor, ano, fk_usuario) VALUES
(1, 'ABC1234', 'Civic',   'Honda',    'Médio',   'Preto',  '2020', 1),
(2, 'DEF5678', 'Corolla', 'Toyota',   'Médio',   'Branco', '2019', 2),
(3, 'GHI9012', 'HB20',   'Hyundai',  'Pequeno', 'Prata',  '2021', 1);

-- ORDEM DE SERVIÇO (criada para cobrir cenários de busca/delete)
INSERT INTO ordem_servico (id, data_agendamento, preco_minimo, observacoes, fk_veiculo, fk_status, fk_motivo) VALUES
(1, '2025-12-01 10:00:00', 150.00, 'Teste de integração', 1, 1, NULL),
(2, '2025-12-02 14:00:00', 200.00, 'Outro agendamento',   2, 2, NULL);

-- ITENS DE SERVIÇO
INSERT INTO item_servico (id, ordem_servico_id, servico_id, preco) VALUES
(1, 1, 1, 25.00),
(2, 1, 3, 80.00),
(3, 2, 2, 45.00);

-- CARRINHO (pessoa 1 com serviço 2)
INSERT INTO carrinho (id, fk_pessoa, fk_servico) VALUES
(1, 1, 2);

-- FAVORITOS (pessoa 1 favoritou serviço 3)
INSERT INTO favorito (id, fk_pessoa, fk_servico) VALUES
(1, 1, 3);
