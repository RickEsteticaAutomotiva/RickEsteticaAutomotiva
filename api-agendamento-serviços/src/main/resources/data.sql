-- Dados fictícios para tabela pessoa
INSERT INTO pessoa (id, nome, cpf, email, telefone, data_nascimento, senha) VALUES
(1, 'João Silva', '12345678901', 'joao.silva@email.com', '11987654321', '1990-05-15', 'senha123'),
(2, 'Maria Santos', '23456789012', 'maria.santos@email.com', '11876543210', '1985-08-20', 'senha123'),
(3, 'Pedro Oliveira', '34567890123', 'pedro.oliveira@email.com', '11765432109', '1992-12-10', 'senha123'),
(4, 'Ana Costa', '45678901234', 'ana.costa@email.com', '11654321098', '1988-03-25', 'senha123'),
(5, 'Carlos Ferreira', '56789012345', 'carlos.ferreira@email.com', '11543210987', '1995-07-08', 'senha123');

-- Dados fictícios para tabela servico
INSERT INTO servico (id, nome, descricao, preco) VALUES
(1, 'Lavagem Simples', 'Lavagem externa básica do veículo', 25.00),
(2, 'Lavagem Completa', 'Lavagem externa e interna completa', 45.00),
(3, 'Enceramento', 'Aplicação de cera protetora na pintura', 80.00),
(4, 'Lavagem + Cera', 'Lavagem completa com enceramento', 120.00),
(5, 'Detalhamento Completo', 'Serviço completo de detalhamento automotivo', 200.00);

-- Dados fictícios para tabela veiculo
INSERT INTO veiculo (id, placa, modelo, marca, porte, cor, ano, fk_usuario) VALUES
(1, 'ABC1234', 'Civic', 'Honda', 'Médio', 'Preto', '2020', 1),
(2, 'DEF5678', 'Corolla', 'Toyota', 'Médio', 'Branco', '2019', 2),
(3, 'GHI9012', 'HB20', 'Hyundai', 'Pequeno', 'Prata', '2021', 3),
(4, 'JKL3456', 'Hilux', 'Toyota', 'Grande', 'Azul', '2022', 4),
(5, 'MNO7890', 'Onix', 'Chevrolet', 'Pequeno', 'Vermelho', '2020', 5);

-- Dados fictícios para tabela STATUS
INSERT INTO status (id, descricao) VALUES
    (1, 'CONCLUIDO'),
    (2, 'EM_ANDAMENTO'),
    (3, 'CANCELADO');

-- Dados fictícios para tabela MOTIVO_CANCELAMENTO
INSERT INTO motivo (id, descricao) VALUES
    (1, 'DESISTENCIA'),
    (2, 'PROBLEMA_TECNICO'),
    (3, 'REAGENDAMENTO');

-- Dados fictícios para tabela ordem_servico
INSERT INTO ordem_servico (id, dt_conclusao, observacoes, data_agendamento, fk_veiculo, fk_status, fk_motivo) VALUES
    (1, '2024-01-20 15:00:00', 'Serviço executado conforme solicitado', '2024-01-18 10:00:00', 1, 1, NULL),
    (2, NULL, 'Aguardando peças', '2024-01-19 09:30:00', 2, 3, 1),
    (3, '2024-01-22 17:15:00', 'Cliente muito satisfeito', '2024-01-20 08:00:00', 3, 1, NULL),
    (4, NULL, 'Iniciado hoje', '2024-01-21 11:45:00', 4, 2, NULL),
    (5, '2024-01-25 16:30:00', 'Excelente resultado', '2024-01-23 14:00:00', 5, 1, NULL);

-- Dados fictícios para tabela item_servico
INSERT INTO item_servico (id, ordem_servico_id, servico_id, preco) VALUES
(1, 1, 1, 25.00),
(2, 1, 3, 80.00),
(3, 2, 2, 45.00),
(4, 3, 4, 120.00),
(5, 4, 5, 200.00),
(6, 5, 2, 45.00),
(7, 5, 3, 80.00);