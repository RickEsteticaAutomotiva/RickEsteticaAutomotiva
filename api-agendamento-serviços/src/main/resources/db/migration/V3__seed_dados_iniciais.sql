-- =====================================================
-- V3__seed_dados_iniciais.sql
-- Seed inicial do sistema Rick Estética Automotiva
-- Compatível com MySQL 8+
-- =====================================================

-- =====================================================
-- CATEGORIAS
-- =====================================================
INSERT INTO categoria (id, nome)
VALUES
    (1, 'Lavagem'),
    (2, 'Polimento'),
    (3, 'Detalhamento');

-- =====================================================
-- STATUS
-- =====================================================
INSERT INTO status (id, descricao)
VALUES
    (1, 'ANÁLISE'),
    (2, 'AGENDA CONFIRMADA'),
    (3, 'EM EXECUÇÃO'),
    (4, 'CANCELADO'),
    (5, 'CONCLUÍDO');

-- =====================================================
-- MOTIVOS
-- =====================================================
INSERT INTO motivo (descricao)
VALUES
    ('DESISTENCIA'),
    ('PROBLEMA_TECNICO'),
    ('REAGENDAMENTO');

-- =====================================================
-- SERVIÇOS
-- =====================================================
INSERT INTO servico (
    nome,
    descricao,
    preco,
    fk_categoria
)
VALUES
    ('Lavagem Simples','Lavagem externa básica do veículo',25.00,1),
    ('Lavagem Completa','Lavagem externa e interna completa',45.00,1),
    ('Enceramento','Aplicação de cera protetora na pintura',80.00,2),
    ('Lavagem + Cera','Lavagem completa com enceramento',120.00,2),
    ('Detalhamento Completo','Serviço completo de detalhamento automotivo',200.00,3);