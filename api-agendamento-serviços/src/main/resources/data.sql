-- ===============================
-- DADOS FICTÍCIOS - SISTEMA AUTOMOTIVA
-- ===============================

-- TABELA: pessoa
INSERT INTO pessoa (nome, cpf, email, telefone, data_nascimento, senha) VALUES
('Rodrigo Santos', '12345678901', 'rodrigoapolodev@gmail.com', '11987654321', '1990-05-15', '$2a$10$351eVELULypd9x8ong42rOgTIMdw6sitDwqIvRIFGVDHi7cXRIGL2'),
('Maria Santos', '23456789012', 'maria.santos@email.com', '11876543210', '1985-08-20', 'senha123'),
('Pedro Oliveira', '34567890123', 'pedro.oliveira@email.com', '11765432109', '1992-12-10', 'senha123'),
('Ana Costa', '45678901234', 'ana.costa@email.com', '11654321098', '1988-03-25', 'senha123'),
('Carlos Ferreira', '56789012345', 'carlos.ferreira@email.com', '11543210987', '1995-07-08', 'senha123');


-- Inserir categorias primeiro
INSERT INTO categoria (id, nome) VALUES
(1, 'Lavagem'),
(2, 'Polimento'),
(3, 'Detalhamento');

-- Depois inserir serviços com as categorias
INSERT INTO servico (nome, descricao, preco, fk_categoria, margem_lucro) VALUES
('Lavagem Simples', 'Lavagem externa básica do veículo', 25.00, 1, 0.20),
('Lavagem Completa', 'Lavagem externa e interna completa', 45.00, 1, 0.25),
('Enceramento', 'Aplicação de cera protetora na pintura', 80.00, 2, 0.30),
('Lavagem + Cera', 'Lavagem completa com enceramento', 120.00, 2, 0.30),
('Detalhamento Completo', 'Serviço completo de detalhamento automotivo', 200.00, 3, 0.35);


-- ===============================
-- TABELA: veiculo
INSERT INTO veiculo (placa, modelo, marca, porte, cor, ano, fk_usuario) VALUES
('ABC1234', 'Civic', 'Honda', 'Médio', 'Preto', 2020, 1),
('DEF5678', 'Corolla', 'Toyota', 'Médio', 'Branco', 2019, 2),
('GHI9012', 'HB20', 'Hyundai', 'Pequeno', 'Prata', 2021, 3),
('JKL3456', 'Hilux', 'Toyota', 'Grande', 'Azul', 2022, 4),
('MNO7890', 'Onix', 'Chevrolet', 'Pequeno', 'Vermelho', 2020, 5);

-- ===============================
-- TABELA: status
INSERT INTO status (id, descricao) VALUES
(1, 'ANÁLISE'),
(2, 'AGENDA CONFIRMADA'),
(3, 'EM EXECUÇÃO'),
(4, 'CANCELADO'),
(5, 'CONCLUÍDO');

-- ===============================
-- TABELA: motivo_cancelamento
INSERT INTO motivo (descricao) VALUES
('DESISTENCIA'),
('PROBLEMA_TECNICO'),
('REAGENDAMENTO');

-- ===============================
-- TABELA: ordem_servico
-- Ajustado conforme a entidade OrdemServicoEntity
INSERT INTO ordem_servico (
    data_agendamento,
    preco_minimo,
    observacoes,
    dt_conclusao,
    fk_veiculo,
    fk_status,
    fk_motivo
) VALUES
('2024-01-18 10:00:00', 105.00, 'Serviço executado conforme solicitado', '2024-01-20 15:00:00', 1, 1, NULL),
('2024-01-19 09:30:00', 45.00, 'Aguardando peças', NULL, 2, 3, 1),
('2024-01-20 08:00:00', 120.00, 'Cliente muito satisfeito', '2024-01-22 17:15:00', 3, 1, NULL),
('2024-01-21 11:45:00', 200.00, 'Iniciado hoje', NULL, 4, 2, NULL),
('2024-01-23 14:00:00', 125.00, 'Excelente resultado', '2024-01-25 16:30:00', 5, 1, NULL),
('2024-10-02 09:15:00', 80.00, 'Revisão periódica realizada', '2024-10-03 14:40:00', 1, 1, NULL),
('2024-10-04 11:30:00', 0.00, 'Aguardando autorização do cliente', NULL, 3, 2, NULL),
('2024-10-06 08:50:00', 150.00, 'Troca de óleo concluída', '2024-10-06 12:20:00', 2, 1, NULL),
('2024-10-10 15:10:00', 0.00, 'Servico iniciado, aguardando peças', NULL, 4, 3, NULL),
('2024-10-12 10:00:00', 275.00, 'Cliente relatou ruído na suspensão', '2024-10-14 16:30:00', 5, 1, NULL),
('2025-10-04 09:20:00', 100.00, 'Lavagem completa realizada', '2025-10-04 12:15:00', 2, 1, NULL),
('2025-10-15 14:10:00', 100.00, 'Cliente solicitou avaliação prévia', NULL, 3, 5, NULL),
('2025-10-27 16:45:00', 200.00, 'Correção de pequenos riscos iniciada', NULL, 1, 3, NULL),
('2025-10-06 10:30:00', 150.00, 'Polimento básico concluído', '2025-10-06 13:00:00', 4, 1, NULL),
('2025-10-12 09:00:00', 180.00, 'Higienização interna em andamento', NULL, 1, 2, NULL),
('2025-10-18 15:40:00', 220.00, 'Cliente solicitou retoque adicional', '2025-10-19 11:20:00', 3, 1, NULL),
('2025-10-29 17:10:00', 300.00, 'Reparo finalizado com sucesso', '2025-10-30 09:45:00', 2, 1, NULL),
('2025-11-05 09:00:00', 1000.00, 'Revisão completa realizada', '2025-11-06 14:30:00', 2, 1, NULL),
('2025-11-05 09:00:00', 100.00, 'Revisão completa realizada', '2025-11-06 14:30:00', 2, 1, NULL),
('2025-11-12 13:45:00', 1200.00, 'Aguardando aprovação do cliente', NULL, 3, 5, NULL),
('2025-11-25 16:20:00', 200.00, 'Serviço urgente iniciado', NULL, 1, 5, NULL),
('2025-11-28 10:30:00', 180.00, 'Cliente desistiu antes do início', NULL, 1, 4, 1),
('2025-11-29 09:00:00', 350.00, '  devido a problemas técnicos no equipamento', NULL, 2, 4, 2),
('2025-12-02 16:40:00', 220.00, 'Cancelado: cliente encontrou preço menor', NULL, 4, 4, 1),
('2025-12-03 11:20:00', 150.00, 'Cancelamento por falha elétrica no veículo', NULL, 5, 4, 2),
('2025-12-02 09:30:00', 500.00, 'Inspeção e ajuste, concluído', '2025-12-02 12:00:00', 1, 5, NULL),
('2025-12-03 14:45:00', 160.00, 'Reparo estético finalizado', '2025-12-03 17:00:00', 2, 5, NULL),
('2025-12-04 11:15:00', 190.00, 'Limpeza profunda concluída', '2025-12-04 14:20:00', 3, 5, NULL),
('2025-12-05 16:00:00', 260.00, 'Revisão completa finalizada', '2025-12-05 18:45:00', 4, 5, NULL),
('2025-12-06 10:40:00', 220.00, 'Higienização interna concluída', '2025-12-06 13:00:00', 5, 5, NULL),
('2025-11-25 16:30:00', 200.00, 'Serviço urgente iniciado', '2025-12-06 13:30:00', 1, 5, NULL),
('2025-12-07 10:40:00', 220.00, 'Higienização interna concluída', '2025-12-06 13:00:00', 5, 5, NULL);

-- ===============================
-- TABELA: item_servico
-- ===============================
-- TABELA: item_servico (COMPLETO E CONSISTENTE)
-- ===============================

INSERT INTO item_servico (ordem_servico_id, servico_id, preco) VALUES
-- já existentes (mantidos)
(1, 1, 25.00),
(1, 3, 80.00),
(2, 2, 45.00),
(3, 4, 120.00),
(4, 5, 200.00),
(5, 2, 45.00),
(5, 3, 80.00),
(11, 1, 50.00),
(11, 2, 50.00),
(12, 3, 60.00),
(12, 4, 40.00),
(13, 5, 100.00),
(13, 5, 100.00),
(14, 1, 50.00),
(14, 2, 100.00),
(18, 4, 500.00),
(18, 3, 500.00),
(19, 2, 100.00),
(20, 2, 200.00),
(20, 2, 900.00),
(7, 2, 45.00),
(9, 4, 120.00),
(10, 3, 80.00),
(6, 3, 80.00),
(8, 5, 150.00),
(15, 5, 180.00),
(16, 5, 220.00),
(17, 5, 300.00),
(21, 4, 120.00),
(21, 1, 80.00),
(22, 4, 120.00),
(22, 1, 60.00),
(23, 5, 200.00),
(23, 3, 150.00),
(24, 5, 220.00),
(25, 3, 80.00),
(25, 1, 70.00),
(26, 5, 300.00),
(26, 4, 200.00),
(27, 2, 80.00),
(27, 1, 80.00),
(28, 3, 80.00),
(28, 1, 110.00),
(29, 5, 260.00),
(30, 5, 220.00),
(31, 4, 120.00),
(31, 3, 80.00),
(32, 5, 220.00);