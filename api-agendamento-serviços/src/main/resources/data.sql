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
INSERT INTO servico (nome, descricao, preco, fk_categoria) VALUES
('Lavagem Simples', 'Lavagem externa básica do veículo', 25.00, 1),
('Lavagem Completa', 'Lavagem externa e interna completa', 45.00, 1),
('Enceramento', 'Aplicação de cera protetora na pintura', 80.00, 2),
('Lavagem + Cera', 'Lavagem completa com enceramento', 120.00, 2),
('Detalhamento Completo', 'Serviço completo de detalhamento automotivo', 200.00, 3);

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
('2024-01-18 10:00:00', 150.00, 'Serviço executado conforme solicitado', '2024-01-20 15:00:00', 1, 1, NULL),
('2024-01-19 09:30:00', 200.00, 'Aguardando peças', NULL, 2, 3, 1),
('2024-01-20 08:00:00', 180.00, 'Cliente muito satisfeito', '2024-01-22 17:15:00', 3, 1, NULL),
('2024-01-21 11:45:00', 120.00, 'Iniciado hoje', NULL, 4, 2, NULL),
('2024-01-23 14:00:00', 250.00, 'Excelente resultado', '2024-01-25 16:30:00', 5, 1, NULL),
('2024-10-02 09:15:00', 180.00, 'Revisão periódica realizada', '2024-10-03 14:40:00', 1, 1, NULL),
('2024-10-04 11:30:00', 220.00, 'Aguardando autorização do cliente', NULL, 3, 2, NULL),
('2024-10-06 08:50:00', 150.00, 'Troca de óleo concluída', '2024-10-06 12:20:00', 2, 1, NULL),
('2024-10-10 15:10:00', 300.00, 'Servico iniciado, aguardando peças', NULL, 4, 3, NULL),
('2024-10-12 10:00:00', 275.00, 'Cliente relatou ruído na suspensão', '2024-10-14 16:30:00', 5, 1, NULL),
('2025-10-04 09:20:00', 100.00, 'Lavagem completa realizada', '2025-10-04 12:15:00', 2, 1, NULL),
('2025-10-15 14:10:00', 100.00, 'Cliente solicitou avaliação prévia', NULL, 3, 5, NULL),
('2025-10-27 16:45:00', 200.00, 'Correção de pequenos riscos iniciada', NULL, 1, 3, NULL),
('2025-10-06 10:30:00', 150.00, 'Polimento básico concluído', '2025-10-06 13:00:00', 4, 1, NULL),
('2025-10-12 09:00:00', 180.00, 'Higienização interna em andamento', NULL, 1, 2, NULL),
('2025-10-18 15:40:00', 220.00, 'Cliente solicitou retoque adicional', '2025-10-19 11:20:00', 3, 1, NULL),
('2025-10-29 17:10:00', 300.00, 'Reparo finalizado com sucesso', '2025-10-30 09:45:00', 2, 1, NULL),
('2025-11-05 09:00:00', 1000.00, 'Revisão completa realizada', '2025-11-06 14:30:00', 2, 1, NULL),
('2025-11-12 13:45:00', 2000.00, 'Aguardando aprovação do cliente', NULL, 3, 5, NULL),
('2025-11-25 16:20:00', 200.00, 'Serviço urgente iniciado', NULL, 1, 5, NULL);
;

-- ===============================
-- TABELA: item_servico
INSERT INTO item_servico (ordem_servico_id, servico_id, preco) VALUES
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
(19, 2, 1000.00),
(20, 2, 100.00);
