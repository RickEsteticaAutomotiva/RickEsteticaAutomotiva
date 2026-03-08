# Guia de Migrações com Flyway

## Estrutura de Diretórios

```
src/main/resources/
└── db/
    ├── migration/          ← scripts versionados (todos os ambientes)
    │   └── V1__create_schema.sql
    └── seed/               ← dados fictícios (APENAS dev)
        └── R__seed_dev.sql
```

## Nomenclatura Obrigatória

| Tipo        | Padrão                                     | Exemplo                                         |
|-------------|--------------------------------------------|-------------------------------------------------|
| Versionado  | `V{N}__{descricao_snake_case}.sql`         | `V2__add_coluna_observacoes_veiculo.sql`        |
| Repetível   | `R__{descricao_snake_case}.sql`            | `R__seed_dev.sql`                               |
| Undo (raro) | `U{N}__{descricao_snake_case}.sql`         | `U2__undo_add_coluna_observacoes_veiculo.sql`   |

> **Regra de ouro:** scripts versionados (`V`) são **imutáveis** após aplicados.  
> Nunca edite um arquivo `V*.sql` que já foi commitado — crie um novo `V{N+1}__`.

## Checklist antes de criar uma migration

- [ ] A versão é sequencial? (verificar a maior versão existente em `db/migration/`)
- [ ] O script é compatível com **MySQL 8+** (dialeto principal do projeto)?
- [ ] Usei `AUTO_INCREMENT` para chaves primárias (padrão MySQL 8, aceito pelo H2 2.x como alias)?
- [ ] Usei `LONGTEXT` para campos grandes (aceito pelo H2 2.x como alias de `CLOB`)?
- [ ] O script é **idempotente** onde necessário? (`CREATE TABLE IF NOT EXISTS`, `ALTER TABLE ... IF NOT EXISTS`)
- [ ] Rodei `mvn flyway:validate` localmente antes do push?
- [ ] A PR inclui a migration **e** a mudança na entidade JPA correspondente?

## Workflows por Ambiente

### Desenvolvimento local (profile `dev`)

```bash
# Subir o banco MySQL via Docker
docker compose -f docker/docker-compose.yml up -d mysql

# Rodar a aplicação — Flyway executa automaticamente as migrations pendentes
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,swagger

# Verificar status das migrations
./mvnw flyway:info -Dflyway.url=jdbc:mysql://localhost:3306/rick_dev \
  -Dflyway.user=rick -Dflyway.password=rick@dev2024

# Aplicar manualmente (sem subir a app)
./mvnw flyway:migrate -Dflyway.url=jdbc:mysql://localhost:3306/rick_dev \
  -Dflyway.user=rick -Dflyway.password=rick@dev2024
```

> **Banco já existente?** Execute uma vez:
> ```bash
> ./mvnw flyway:baseline -Dflyway.baselineVersion=1 \
>   -Dflyway.url=jdbc:mysql://localhost:3306/rick_dev \
>   -Dflyway.user=rick -Dflyway.password=rick@dev2024
> ```

### Testes de Integração (profile `integration-test`)

O Flyway cria o schema H2 automaticamente via `V1__create_schema.sql` ao subir o contexto Spring.  
Os dados são injetados via `@Sql({"/reset-it.sql", "/seed-it.sql"})` — **não** via migration.

```bash
# Rodar apenas testes de integração
./mvnw verify -P integration-test
```

### CI/CD (homolog e prod)

```bash
# Variáveis de ambiente obrigatórias:
# DB_URL, DB_USERNAME, DB_PASSWORD

# Validar antes do deploy
./mvnw flyway:validate

# Migrations aplicadas automaticamente no boot da aplicação
```

## Compatibilidade H2 vs MySQL

O `V1__create_schema.sql` usa sintaxe **MySQL-first**. O H2 2.x aceita os tipos abaixo como aliases nativos, garantindo que os testes de integração funcionem sem scripts distintos:

| Sintaxe usada no projeto | Suporte MySQL 8+ | Suporte H2 2.x (alias) |
|--------------------------|-----------------|------------------------|
| `AUTO_INCREMENT`         | ✅ nativo        | ✅ aceito como alias    |
| `LONGTEXT`               | ✅ nativo        | ✅ aceito como alias    |
| `IF NOT EXISTS`          | ✅               | ✅                      |
| `ENGINE=InnoDB`          | ✅ (omitido)     | ✅ (ignorado)           |

## FAQ

**Q: Posso rodar `flyway:clean` em prod?**  
A: **Não.** O `FlywayConfig.java` define `cleanDisabled=true` e o `application-prod.properties` confirma. Isso destrói todo o banco.

**Q: O checksum de um script mudou e o boot falhou — o que fazer?**  
A: **Não edite o script.** Crie uma nova migration `V{N+1}__` com a correção. Se for absolutamente necessário corrigir, use `./mvnw flyway:repair` apenas em dev.

**Q: Como adicionar uma nova coluna?**  
```sql
-- V2__add_telefone_secundario_pessoa.sql
ALTER TABLE pessoa ADD COLUMN telefone_secundario VARCHAR(20);
```

**Q: Como renomear uma coluna?**  
```sql
-- V3__rename_telefone_pessoa.sql
ALTER TABLE pessoa RENAME COLUMN telefone_secundario TO telefone2;
```

