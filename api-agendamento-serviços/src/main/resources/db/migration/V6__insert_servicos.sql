INSERT INTO servico (nome, descricao, preco, fk_categoria) VALUES
                                                               ('Lavagem Técnica Carro P', 'Lavagem detalhada para veículos pequenos', 70.00, 1),
                                                               ('Lavagem Técnica Carro M', 'Lavagem detalhada para veículos médios', 80.00, 1),
                                                               ('Lavagem Técnica Carro G', 'Lavagem detalhada para veículos grandes', 90.00, 1),
                                                               ('Lavagem Premium', 'Descontaminação ferrosa, rodas e cera líquida', 100.00, 1),
                                                               ('Limpeza Técnica de Motor', 'Limpeza detalhada com proteção do motor', 120.00, 1),
                                                               ('Limpeza Técnica de Chassi', 'Limpeza profunda da parte inferior', 400.00, 1),
                                                               ('Enceramento Técnico', 'Aplicação profissional de cera de alta performance', 200.00, 2),
                                                               ('Polimento de Farol', 'Restauração e proteção UV de faróis', 200.00, 2),
                                                               ('Polimento Técnico', 'Correção de pintura e brilho intenso', 800.00, 2),
                                                               ('Cristalização de Pintura', 'Proteção e espelhamento da lataria', 250.00, 2),
                                                               ('Vitrificação de Pintura', 'Proteção cerâmica de longa duração (lataria)', 1250.00, 2),
                                                               ('Vitrificação de Plásticos', 'Proteção cerâmica para plásticos externos', 350.00, 2),
                                                               ('Vitrificação de Couro', 'Proteção cerâmica para o interior em couro', 300.00, 2),
                                                               ('Vitrificação de Pára-brisa', 'Tratamento hidrofóbico para vidros', 150.00, 2),
                                                               ('Remoção de Chuva Ácida Vidros', 'Remoção de manchas de água nos vidros', 100.00, 2),
                                                               ('Higienização Interna Completa', 'Limpeza profunda de todo o habitáculo', 800.00, 3),
                                                               ('Higienização Teto e Colunas', 'Limpeza específica do forro e colunas', 150.00, 3),
                                                               ('Higienização Bancos Couro/Tecido', 'Limpeza e hidratação de assentos', 180.00, 3),
                                                               ('Oxi Sanitização', 'Eliminação de odores e bactérias com Ozônio', 120.00, 3),
                                                               ('Revitalização de Plásticos Internos', 'Recuperação do brilho original interno', 50.00, 3);

UPDATE servico SET imagem = 'Lavagem_Tecnica_Carro_P' WHERE nome = 'Lavagem Técnica Carro P';
UPDATE servico SET imagem = 'Lavagem_Tecnica_Carro_M' WHERE nome = 'Lavagem Técnica Carro M';
UPDATE servico SET imagem = 'Lavagem_Tecnica_Carro_G' WHERE nome = 'Lavagem Técnica Carro G';
UPDATE servico SET imagem = 'Lavagem_Premium' WHERE nome = 'Lavagem Premium';
UPDATE servico SET imagem = 'Limpeza_Tecnica_De_Motor' WHERE nome = 'Limpeza Técnica de Motor';
UPDATE servico SET imagem = 'Limpeza_Tecnica_De_Chassi' WHERE nome = 'Limpeza Técnica de Chassi';
UPDATE servico SET imagem = 'Enceramento_tecnico' WHERE nome = 'Enceramento Técnico';
UPDATE servico SET imagem = 'Polimento_de_Farol' WHERE nome = 'Polimento de Farol';
UPDATE servico SET imagem = 'Polimento_Tecnica' WHERE nome = 'Polimento Técnico';
UPDATE servico SET imagem = 'Cristalizacao_de_Pintura' WHERE nome = 'Cristalização de Pintura';
UPDATE servico SET imagem = 'Vitrificacao_de_Pintura' WHERE nome = 'Vitrificação de Pintura';
UPDATE servico SET imagem = 'Vitrificacao_de_Couro' WHERE nome = 'Vitrificação de Couro';
UPDATE servico SET imagem = 'Higienizacao_Interna_Completa' WHERE nome = 'Higienização Interna Completa';
UPDATE servico SET imagem = 'Higienizacao_Teto_e_Colunas' WHERE nome = 'Higienização Teto e Colunas';
UPDATE servico SET imagem = 'Revitalizacao_de_Plasticos_Internos' WHERE nome = 'Revitalização de Plásticos Internos';
