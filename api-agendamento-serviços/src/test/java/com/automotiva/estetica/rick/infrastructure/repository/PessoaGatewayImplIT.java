package com.automotiva.estetica.rick.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import com.automotiva.estetica.rick.infrastructure.gateway.PessoaGatewayImpl;
import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.RoleEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.PessoaEntityMapperImpl;
import com.automotiva.estetica.rick.infrastructure.repository.pessoa.RoleRepository;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de persistÃªncia isolado com @DataJpaTest.
 *
 * <p>
 * Sobe apenas a camada JPA (sem servidor web), usando H2 em memÃ³ria. Valida
 * queries, specifications e o mapeamento Entity â†” Domain Object.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({PessoaGatewayImpl.class, PessoaEntityMapperImpl.class})
@DisplayName("PersistÃªncia â€” PessoaGatewayImpl")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class PessoaGatewayImplIT {

    @Autowired
    private TestEntityManager entityManager;

    /**
     * {@link EntityManager} bruto â€” necessÃ¡rio para queries SQL nativas que
     * ignoram {@code @SQLRestriction}.
     */
    @Autowired
    private EntityManager em;

    @Autowired
    private PessoaGatewayImpl pessoaGateway;

    /**
     * Persiste uma {@link PessoaEntity} sem roles via {@link TestEntityManager}.
     *
     * <p>
     * O campo {@code roles} Ã© deixado com o valor default
     * ({@code new HashSet<>()}) porque o {@code TestEntityManager} opera fora do
     * {@link PessoaGatewayImpl}, que Ã© quem resolve as {@link RoleEntity} pelo
     * {@link RoleRepository}. Para testes que nÃ£o exercitam roles, isso Ã©
     * suficiente.
     */
    private PessoaEntity criarPessoaJpa(String nome, String cpf, String email) {
        return entityManager.persistFlushFind(PessoaEntity.builder().nome(nome).cpf(cpf).email(email)
                .telefone("11999990000").dataNascimento(LocalDate.of(1990, 1, 1)).senha("$2a$10$hash").build());
    }

    /**
     * Persiste uma {@link PessoaEntity} com roles via {@link TestEntityManager}.
     *
     * <p>
     * Cada {@link RoleEnum} informado Ã© primeiro persistido como
     * {@link RoleEntity} (ou buscado, se jÃ¡ existir), garantindo integridade
     * referencial na tabela {@code pessoa_roles} sem depender do
     * {@link PessoaGatewayImpl}.
     */
    private PessoaEntity criarPessoaJpaComRoles(String nome, String cpf, String email, EnumSet<RoleEnum> roles) {
        Set<RoleEntity> roleEntities = roles.stream().map(roleEnum -> {
            RoleEntity role = RoleEntity.builder().nome(roleEnum).build();
            return entityManager.persistFlushFind(role);
        }).collect(Collectors.toSet());

        return entityManager
                .persistFlushFind(PessoaEntity.builder().nome(nome).cpf(cpf).email(email).telefone("11999990001")
                        .dataNascimento(LocalDate.of(1990, 1, 1)).senha("$2a$10$hash").roles(roleEntities).build());
    }

    @Test
    @DisplayName("salvar â†’ persiste e retorna domÃ­nio com ID gerado")
    void salvar_sucesso() {
        Pessoa pessoa = Pessoa.builder().nome("Novo UsuÃ¡rio").cpf("11122233344").email("novo@email.com")
                .telefone("11988887777").dataNascimento(LocalDate.of(1995, 6, 20)).senha("$2a$10$hash").build();

        Pessoa salva = pessoaGateway.salvar(pessoa);

        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getEmail()).isEqualTo("novo@email.com");
        assertThat(salva.getNome()).isEqualTo("Novo UsuÃ¡rio");
    }

    @Test
    @DisplayName("buscarPorId â†’ retorna Optional com pessoa quando ID existe")
    void buscarPorId_encontrado() {
        PessoaEntity jpa = criarPessoaJpa("JoÃ£o Teste", "99988877700", "joao@teste.com");

        Optional<Pessoa> resultado = pessoaGateway.buscarPorId(jpa.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("joao@teste.com");
    }

    @Test
    @DisplayName("buscarPorId â†’ retorna Optional vazio quando ID nÃ£o existe")
    void buscarPorId_naoEncontrado() {
        Optional<Pessoa> resultado = pessoaGateway.buscarPorId(99999L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorEmail â†’ retorna Optional com pessoa quando e-mail existe")
    void buscarPorEmail_encontrado() {
        criarPessoaJpa("Maria Email", "88877766600", "maria@email.com");

        Optional<Pessoa> resultado = pessoaGateway.buscarPorEmail("maria@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria Email");
    }

    @Test
    @DisplayName("buscarPorEmail â†’ retorna Optional vazio quando e-mail nÃ£o existe")
    void buscarPorEmail_naoEncontrado() {
        Optional<Pessoa> resultado = pessoaGateway.buscarPorEmail("inexistente@email.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("existePorCpf â†’ retorna true quando CPF cadastrado")
    void existePorCpf_existente() {
        criarPessoaJpa("CPF Teste", "77766655500", "cpf@teste.com");

        assertThat(pessoaGateway.existePorCpf("77766655500")).isTrue();
    }

    @Test
    @DisplayName("existePorCpf â†’ retorna false quando CPF nÃ£o cadastrado")
    void existePorCpf_naoExistente() {
        assertThat(pessoaGateway.existePorCpf("00000000000")).isFalse();
    }

    @Test
    @DisplayName("existePorEmail â†’ retorna true quando e-mail cadastrado")
    void existePorEmail_existente() {
        criarPessoaJpa("Email Existe", "66655544400", "existe@email.com");

        assertThat(pessoaGateway.existePorEmail("existe@email.com")).isTrue();
    }

    @Test
    @DisplayName("existePorId â†’ retorna true quando ID existe")
    void existePorId_existente() {
        PessoaEntity jpa = criarPessoaJpa("Pessoa ID", "55544433300", "id@email.com");

        assertThat(pessoaGateway.existePorId(jpa.getId())).isTrue();
    }

    @Test
    @DisplayName("existePorId â†’ retorna false quando ID nÃ£o existe")
    void existePorId_naoExistente() {
        assertThat(pessoaGateway.existePorId(99999L)).isFalse();
    }

    @Test
    @DisplayName("buscarTodos â†’ filtra por nome via specification")
    void buscarTodos_filtroNome() {
        criarPessoaJpa("Carlos Spec", "44433322200", "carlos.spec@email.com");
        criarPessoaJpa("Ana Spec", "33322211100", "ana.spec@email.com");

        Page<Pessoa> resultado = pessoaGateway.buscarTodos("Carlos", PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty()
                .allSatisfy(p -> assertThat(p.getNome()).containsIgnoringCase("Carlos"));
    }

    @Test
    @DisplayName("buscarTodos â†’ retorna pÃ¡gina completa quando filtro Ã© nulo")
    void buscarTodos_semFiltro() {
        criarPessoaJpa("Fulano Sem Filtro", "22211100011", "fulano@email.com");

        Page<Pessoa> resultado = pessoaGateway.buscarTodos(null, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("deletarPorId â†’ aplica soft-delete preenchendo deletadoEm e oculta da busca")
    void deletarPorId_sucesso() {
        PessoaEntity jpa = criarPessoaJpa("Delete Me", "11100099900", "deleteme@email.com");
        Long id = jpa.getId();

        pessoaGateway.deletarPorId(id);
        entityManager.flush();
        entityManager.clear();

        // Query SQL nativa: bypassa o @SQLRestriction("deletado_em IS NULL") do
        // Hibernate.
        // entityManager.find() e qualquer mÃ©todo JPA derivado aplicam o filtro
        // automaticamente,
        // tornando o registro invisÃ­vel apÃ³s o soft-delete â€” por isso nÃ£o podem
        // ser
        // usados aqui.
        // O H2 retorna java.sql.Timestamp para colunas TIMESTAMP em queries nativas,
        // por isso a conversÃ£o explÃ­cita para LocalDateTime Ã© necessÃ¡ria.
        Object resultado = em.createNativeQuery("SELECT deletado_em FROM pessoa WHERE id = :id").setParameter("id", id)
                .getSingleResult();

        LocalDateTime deletadoEm = resultado instanceof java.sql.Timestamp ts
                ? ts.toLocalDateTime()
                : (LocalDateTime) resultado;

        assertThat(deletadoEm).isNotNull();

        // @SQLRestriction oculta o registro nas queries JPA normais
        assertThat(pessoaGateway.buscarPorId(id)).isEmpty();
        assertThat(pessoaGateway.existePorId(id)).isFalse();
    }

    @Test
    @DisplayName("salvar â†’ persiste mÃºltiplas roles e recupera todas via buscarPorId")
    void salvar_multiRole_devePersistirERecuperarTodasRoles() {
        Pessoa pessoa = Pessoa.builder().nome("Admin Gerente").cpf("98765432100").email("admin.gerente@email.com")
                .telefone("11988880000").dataNascimento(LocalDate.of(1988, 7, 15)).senha("$2a$10$hash")
                .roles(EnumSet.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE, RoleEnum.ROLE_CLIENTE)).build();

        Pessoa salva = pessoaGateway.salvar(pessoa);
        entityManager.flush();
        entityManager.clear(); // limpa cache para forÃ§ar leitura do banco

        Optional<Pessoa> recuperada = pessoaGateway.buscarPorId(salva.getId());

        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getRoles()).hasSize(3).contains(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE,
                RoleEnum.ROLE_CLIENTE);
    }

    @Test
    @DisplayName("salvar â†’ role padrÃ£o ROLE_CLIENTE Ã© persistida quando nenhuma role informada")
    void salvar_semRoles_devePersistirRoleUserDefault() {
        // O builder default de Pessoa jÃ¡ atribui ROLE_CLIENTE via @Builder.Default
        Pessoa pessoa = Pessoa.builder().nome("UsuÃ¡rio PadrÃ£o").cpf("12312312312").email("padrao@email.com")
                .senha("$2a$10$hash").build();

        Pessoa salva = pessoaGateway.salvar(pessoa);
        entityManager.flush();
        entityManager.clear();

        Optional<Pessoa> recuperada = pessoaGateway.buscarPorId(salva.getId());

        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getRoles()).hasSize(1).contains(RoleEnum.ROLE_CLIENTE);
    }

    @Test
    @DisplayName("buscarPorEmail â†’ retorna roles corretas quando persistidas via TestEntityManager")
    void buscarPorEmail_comRoles_deveRetornarRolesCorretas() {
        criarPessoaJpaComRoles("Admin Direto", "10000000001", "admin.direto@email.com",
                EnumSet.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE));

        Optional<Pessoa> resultado = pessoaGateway.buscarPorEmail("admin.direto@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getRoles()).hasSize(2).contains(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE);
    }
}
