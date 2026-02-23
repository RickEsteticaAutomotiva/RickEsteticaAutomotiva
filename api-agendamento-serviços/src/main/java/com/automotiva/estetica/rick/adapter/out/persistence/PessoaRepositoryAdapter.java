package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.PessoaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpa.RoleJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.PessoaPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de saída que implementa {@link PessoaRepositoryPort}.
 *
 * <p>Responsável por traduzir chamadas do domínio em operações JPA,
 * incluindo a resolução das {@link RoleJpaEntity} a partir do {@link RoleEnum} do domínio.
 * As roles são buscadas (ou criadas, se ausentes) pelo {@link RoleJpaRepository}
 * para garantir integridade referencial na tabela de junção {@code pessoa_roles}.
 *
 * <p>Camada: adapter/out/persistence.
 */
@Repository
@RequiredArgsConstructor
public class PessoaRepositoryAdapter implements PessoaRepositoryPort {

    private final PessoaJpaRepository jpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PessoaPersistenceMapper mapper;

    @Override
    public Pessoa salvar(Pessoa pessoa) {
        PessoaJpaEntity entity = mapper.toJpaEntity(pessoa);
        entity.setRoles(resolverRoles(pessoa.getRoles()));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Pessoa> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Pessoa> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Page<Pessoa> buscarTodos(String filtro, Pageable pageable) {
        return jpaRepository
                .findAll(PessoaSpecification.filtroUnico(filtro), pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existePorCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deletarPorId(Long id) {
        jpaRepository.deleteById(id);
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    /**
     * Converte {@code Set<RoleEnum>} em {@code Set<RoleJpaEntity>} buscando cada role
     * pelo nome na tabela {@code role}. Se a role ainda não existir no banco (cenário
     * de primeiro deploy ou banco zerado), ela é criada automaticamente via {@code save}.
     *
     * <p>Garante que nunca sejam inseridas entidades duplicadas na tabela {@code role}.
     */
    private Set<RoleJpaEntity> resolverRoles(Set<RoleEnum> roles) {
        Set<RoleEnum> efetivas =
                (roles != null && !roles.isEmpty()) ? roles : EnumSet.of(RoleEnum.ROLE_CLIENTE);

        return efetivas.stream()
                .map(roleEnum -> roleJpaRepository
                        .findByNome(roleEnum)
                        .orElseGet(() -> roleJpaRepository.save(
                                RoleJpaEntity.builder().nome(roleEnum).build())))
                .collect(Collectors.toSet());
    }
}
