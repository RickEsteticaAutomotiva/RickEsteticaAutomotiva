-- seed-extra-pessoa.sql — insere pessoa temporária para testar DELETE sem impactar os seeds principais
-- Usa id=100 para ficar longe dos ids do seed-it.sql (max=2) e da sequence reiniciada em 10.
-- Assume que as roles já foram inseridas pelo seed-it.sql (role id=3 = ROLE_CLIENTE)
INSERT INTO pessoa (id, nome, cpf, email, telefone, data_nascimento, senha) VALUES
(100, 'Temp Delete', '55566677788', 'temp.delete@email.com', '11900000000', '2000-01-01',
 '$2a$10$XlyEDctOlzD7k4GvNxvDv.3NQ7K0SZ0DMdXMhfKBMjJiV3z5gPGKm');

INSERT INTO pessoa_roles (pessoa_id, role_id) VALUES (100, 3);

-- Avança a sequence para além do id=100 inserido explicitamente
ALTER TABLE pessoa ALTER COLUMN id RESTART WITH 110;

