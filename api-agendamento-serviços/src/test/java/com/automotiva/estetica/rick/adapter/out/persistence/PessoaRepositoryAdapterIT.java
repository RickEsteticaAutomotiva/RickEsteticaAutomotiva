package com.automotiva.estetica.rick.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.RoleJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.PessoaPersistenceMapperImpl;
import com.automotiva.estetica.rick.adapter.out.persistence.pessoa.PessoaRepositoryAdapter;
import com.automotiva.estetica.rick.adapter.out.persistence.pessoa.RoleJpaRepository;
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
 * Teste de persistência isolado com @DataJpaTest.
 *
 * <p>Sobe apenas a camada JPA (sem servidor web), usando H2 em memória.
 * Valida queries, specifications e o mapeamento Entity ↔ Domain Object.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({PessoaRepositoryAdapter.class, PessoaPersistenceMapperImpl.class})
@DisplayName("Persistência — PessoaRepositoryAdapter")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class PessoaRepositoryAdapterIT {

    @Autowired
    private TestEntityManager entityManager;

    /** {@link EntityManager} bruto — necessário para queries SQL nativas que ignoram {@code @SQLRestriction}. */
    @Autowired
    private EntityManager em;

    @Autowired
    private PessoaRepositoryAdapter repositoryAdapter;

    /**
     * Persiste uma {@link PessoaJpaEntity} sem roles via {@link TestEntityManager}.
     *
     * <p>O campo {@code roles} é deixado com o valor default ({@code new HashSet<>()})
     * porque o {@code TestEntityManager} opera fora do {@link PessoaRepositoryAdapter},
     * que é quem resolve as {@link RoleJpaEntity} pelo {@link RoleJpaRepository}.
     * Para testes que não exercitam roles, isso é suficiente.
     */
    private PessoaJpaEntity criarPessoaJpa(String nome, String cpf, String email) {
        return entityManager.persistFlushFind(PessoaJpaEntity.builder()
                .nome(nome)
                .cpf(cpf)
                .email(email)
                .telefone("11999990000")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .senha("$2a$10$hash")
                .build());
    }

    /**
     * Persiste uma {@link PessoaJpaEntity} com roles via {@link TestEntityManager}.
     *
     * <p>Cada {@link RoleEnum} informado é primeiro persistido como {@link RoleJpaEntity}
     * (ou buscado, se já existir), garantindo integridade referencial na tabela
     * {@code pessoa_roles} sem depender do {@link PessoaRepositoryAdapter}.
     */
    private PessoaJpaEntity criarPessoaJpaComRoles(String nome, String cpf, String email, EnumSet<RoleEnum> roles) {
        Set<RoleJpaEntity> roleEntities = roles.stream()
                .map(roleEnum -> {
                    RoleJpaEntity role = RoleJpaEntity.builder().nome(roleEnum).build();
                    return entityManager.persistFlushFind(role);
                })
                .collect(Collectors.toSet());

        return entityManager.persistFlushFind(PessoaJpaEntity.builder()
                .nome(nome)
                .cpf(cpf)
                .email(email)
                .telefone("11999990001")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .senha("$2a$10$hash")
                .roles(roleEntities)
                .build());
    }

    @Test
    @DisplayName("salvar → persiste e retorna domínio com ID gerado")
    void salvar_sucesso() {
        Pessoa pessoa = Pessoa.builder()
                .nome("Novo Usuário")
                .cpf("11122233344")
                .email("novo@email.com")
                .telefone("11988887777")
                .dataNascimento(LocalDate.of(1995, 6, 20))
                .senha("$2a$10$hash")
                .build();

        Pessoa salva = repositoryAdapter.salvar(pessoa);

        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getEmail()).isEqualTo("novo@email.com");
        assertThat(salva.getNome()).isEqualTo("Novo Usuário");
    }

    @Test
    @DisplayName("buscarPorId → retorna Optional com pessoa quando ID existe")
    void buscarPorId_encontrado() {
        PessoaJpaEntity jpa = criarPessoaJpa("João Teste", "99988877700", "joao@teste.com");

        Optional<Pessoa> resultado = repositoryAdapter.buscarPorId(jpa.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("joao@teste.com");
    }

    @Test
    @DisplayName("buscarPorId → retorna Optional vazio quando ID não existe")
    void buscarPorId_naoEncontrado() {
        Optional<Pessoa> resultado = repositoryAdapter.buscarPorId(99999L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("buscarPorEmail → retorna Optional com pessoa quando e-mail existe")
    void buscarPorEmail_encontrado() {
        criarPessoaJpa("Maria Email", "88877766600", "maria@email.com");

        Optional<Pessoa> resultado = repositoryAdapter.buscarPorEmail("maria@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria Email");
    }

    @Test
    @DisplayName("buscarPorEmail → retorna Optional vazio quando e-mail não existe")
    void buscarPorEmail_naoEncontrado() {
        Optional<Pessoa> resultado = repositoryAdapter.buscarPorEmail("inexistente@email.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("existePorCpf → retorna true quando CPF cadastrado")
    void existePorCpf_existente() {
        criarPessoaJpa("CPF Teste", "77766655500", "cpf@teste.com");

        assertThat(repositoryAdapter.existePorCpf("77766655500")).isTrue();
    }

    @Test
    @DisplayName("existePorCpf → retorna false quando CPF não cadastrado")
    void existePorCpf_naoExistente() {
        assertThat(repositoryAdapter.existePorCpf("00000000000")).isFalse();
    }

    @Test
    @DisplayName("existePorEmail → retorna true quando e-mail cadastrado")
    void existePorEmail_existente() {
        criarPessoaJpa("Email Existe", "66655544400", "existe@email.com");

        assertThat(repositoryAdapter.existePorEmail("existe@email.com")).isTrue();
    }

    @Test
    @DisplayName("existePorId → retorna true quando ID existe")
    void existePorId_existente() {
        PessoaJpaEntity jpa = criarPessoaJpa("Pessoa ID", "55544433300", "id@email.com");

        assertThat(repositoryAdapter.existePorId(jpa.getId())).isTrue();
    }

    @Test
    @DisplayName("existePorId → retorna false quando ID não existe")
    void existePorId_naoExistente() {
        assertThat(repositoryAdapter.existePorId(99999L)).isFalse();
    }

    @Test
    @DisplayName("buscarTodos → filtra por nome via specification")
    void buscarTodos_filtroNome() {
        criarPessoaJpa("Carlos Spec", "44433322200", "carlos.spec@email.com");
        criarPessoaJpa("Ana Spec", "33322211100", "ana.spec@email.com");

        Page<Pessoa> resultado = repositoryAdapter.buscarTodos("Carlos", PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty().allSatisfy(p -> assertThat(p.getNome())
                .containsIgnoringCase("Carlos"));
    }

    @Test
    @DisplayName("buscarTodos → retorna página completa quando filtro é nulo")
    void buscarTodos_semFiltro() {
        criarPessoaJpa("Fulano Sem Filtro", "22211100011", "fulano@email.com");

        Page<Pessoa> resultado = repositoryAdapter.buscarTodos(null, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("deletarPorId → aplica soft-delete preenchendo deletadoEm e oculta da busca")
    void deletarPorId_sucesso() {
        PessoaJpaEntity jpa = criarPessoaJpa("Delete Me", "11100099900", "deleteme@email.com");
        Long id = jpa.getId();

        repositoryAdapter.deletarPorId(id);
        entityManager.flush();
        entityManager.clear();

        // Query SQL nativa: bypassa o @SQLRestriction("deletado_em IS NULL") do Hibernate.
        // entityManager.find() e qualquer método JPA derivado aplicam o filtro automaticamente,
        // tornando o registro invisível após o soft-delete — por isso não podem ser usados aqui.
        // O H2 retorna java.sql.Timestamp para colunas TIMESTAMP em queries nativas,
        // por isso a conversão explícita para LocalDateTime é necessária.
        Object resultado = em
                .createNativeQuery("SELECT deletado_em FROM pessoa WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();

        LocalDateTime deletadoEm = resultado instanceof java.sql.Timestamp ts
                ? ts.toLocalDateTime()
                : (LocalDateTime) resultado;

        assertThat(deletadoEm).isNotNull();

        // @SQLRestriction oculta o registro nas queries JPA normais
        assertThat(repositoryAdapter.buscarPorId(id)).isEmpty();
        assertThat(repositoryAdapter.existePorId(id)).isFalse();
    }

    @Test
    @DisplayName("salvar → persiste múltiplas roles e recupera todas via buscarPorId")
    void salvar_multiRole_devePersistirERecuperarTodasRoles() {
        Pessoa pessoa = Pessoa.builder()
                .nome("Admin Gerente")
                .cpf("98765432100")
                .email("admin.gerente@email.com")
                .telefone("11988880000")
                .dataNascimento(LocalDate.of(1988, 7, 15))
                .senha("$2a$10$hash")
                .roles(EnumSet.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE, RoleEnum.ROLE_CLIENTE))
                .build();

        Pessoa salva = repositoryAdapter.salvar(pessoa);
        entityManager.flush();
        entityManager.clear(); // limpa cache para forçar leitura do banco

        Optional<Pessoa> recuperada = repositoryAdapter.buscarPorId(salva.getId());

        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getRoles())
                .hasSize(3)
                .contains(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE, RoleEnum.ROLE_CLIENTE);
    }

    @Test
    @DisplayName("salvar → role padrão ROLE_CLIENTE é persistida quando nenhuma role informada")
    void salvar_semRoles_devePersistirRoleUserDefault() {
        // O builder default de Pessoa já atribui ROLE_CLIENTE via @Builder.Default
        Pessoa pessoa = Pessoa.builder()
                .nome("Usuário Padrão")
                .cpf("12312312312")
                .email("padrao@email.com")
                .senha("$2a$10$hash")
                .build();

        Pessoa salva = repositoryAdapter.salvar(pessoa);
        entityManager.flush();
        entityManager.clear();

        Optional<Pessoa> recuperada = repositoryAdapter.buscarPorId(salva.getId());

        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getRoles())
                .hasSize(1)
                .contains(RoleEnum.ROLE_CLIENTE);
    }

    @Test
    @DisplayName("buscarPorEmail → retorna roles corretas quando persistidas via TestEntityManager")
    void buscarPorEmail_comRoles_deveRetornarRolesCorretas() {
        criarPessoaJpaComRoles(
                "Admin Direto", "10000000001", "admin.direto@email.com",
                EnumSet.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE));

        Optional<Pessoa> resultado = repositoryAdapter.buscarPorEmail("admin.direto@email.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getRoles())
                .hasSize(2)
                .contains(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_GERENTE);
    }
}
