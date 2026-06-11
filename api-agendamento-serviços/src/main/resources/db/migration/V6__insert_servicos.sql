-- =====================================================
-- Remove serviços placeholder da V3 (não são mais utilizados)
-- =====================================================
DELETE FROM servico;
ALTER TABLE servico AUTO_INCREMENT = 1;

-- =====================================================
-- Novas categorias que não existem (V3 criou: 1-Lavagem, 2-Polimento, 3-Detalhamento)
-- =====================================================
INSERT INTO categoria (id, nome) VALUES
    (4, 'Vitrificação'),
    (5, 'Higienização');

-- =====================================================
-- Catálogo definitivo de serviços Rick Estética Automotiva
-- duracao_minutos baseado no tempo médio real de cada tipo de serviço
-- Categorias: 1-Lavagem | 2-Polimento | 3-Detalhamento | 4-Vitrificação | 5-Higienização
-- =====================================================
INSERT INTO servico (nome, descricao, preco, duracao_minutos, imagem, fk_categoria) VALUES
    ('Lavagem Técnica Carro P',          'Lavagem detalhada para veículos pequenos',              70.00,   45, 'Lavagem_Tecnica_Carro_P',          1),
    ('Lavagem Técnica Carro M',          'Lavagem detalhada para veículos médios',                80.00,   60, 'Lavagem_Tecnica_Carro_M',          1),
    ('Lavagem Técnica Carro G',          'Lavagem detalhada para veículos grandes',               90.00,   75, 'Lavagem_Tecnica_Carro_G',          1),
    ('Lavagem Premium',                  'Descontaminação ferrosa, rodas e cera líquida',        100.00,   90, 'Lavagem_Premium',                  1),
    ('Limpeza Técnica de Motor',         'Limpeza detalhada com proteção do motor',              120.00,   60, 'Limpeza_Tecnica_De_Motor',         1),
    ('Limpeza Técnica de Chassi',        'Limpeza profunda da parte inferior',                   400.00,  120, 'Limpeza_Tecnica_De_Chassi',        1),
    ('Enceramento Técnico',              'Aplicação profissional de cera de alta performance',   200.00,   90, 'Enceramento_tecnico',              2),
    ('Polimento de Farol',               'Restauração e proteção UV de faróis',                  200.00,   60, 'Polimento_de_Farol',               2),
    ('Polimento Técnico',                'Correção de pintura e brilho intenso',                 800.00,  240, 'Polimento_Tecnica',                2),
    ('Cristalização de Pintura',         'Proteção e espelhamento da lataria',                   250.00,  120, 'Cristalizacao_de_Pintura',         2),
    ('Vitrificação de Pintura',          'Proteção cerâmica de longa duração (lataria)',        1250.00,  360, 'Vitrificacao_de_Pintura',          4),
    ('Vitrificação de Plásticos',        'Proteção cerâmica para plásticos externos',            350.00,   90, NULL,                               4),
    ('Vitrificação de Couro',            'Proteção cerâmica para o interior em couro',           300.00,  120, 'Vitrificacao_de_Couro',            4),
    ('Vitrificação de Pára-brisa',       'Tratamento hidrofóbico para vidros',                   150.00,   45, NULL,                               4),
    ('Remoção de Chuva Ácida Vidros',    'Remoção de manchas de água nos vidros',                100.00,   60, NULL,                               2),
    ('Higienização Interna Completa',    'Limpeza profunda de todo o habitáculo',                800.00,  300, 'Higienizacao_Interna_Completa',    5),
    ('Higienização Teto e Colunas',      'Limpeza específica do forro e colunas',                150.00,   90, 'Higienizacao_Teto_e_Colunas',      5),
    ('Higienização Bancos Couro/Tecido', 'Limpeza e hidratação de assentos',                     180.00,  120, NULL,                               5),
    ('Oxi Sanitização',                  'Eliminação de odores e bactérias com Ozônio',          120.00,   40, NULL,                               5),
    ('Revitalização de Plásticos Internos','Recuperação do brilho original interno',               50.00,   30, 'Revitalizacao_de_Plasticos_Internos', 3);
