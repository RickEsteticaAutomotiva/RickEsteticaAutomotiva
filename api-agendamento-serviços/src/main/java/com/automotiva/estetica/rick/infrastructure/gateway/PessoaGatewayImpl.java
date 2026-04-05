package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.RoleEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.PessoaEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.pessoa.PessoaRepository;
import com.automotiva.estetica.rick.infrastructure.repository.pessoa.PessoaSpecification;
import com.automotiva.estetica.rick.infrastructure.repository.pessoa.RoleRepository;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * ImplementaÃ§Ã£o do Gateway de Pessoa. Atua como adaptador de saÃ­da entre
 * domÃ­nio e persistÃªncia JPA.
 *
 * <p>
 * Essa camada garante que o domÃ­nio dependa apenas da abstraÃ§Ã£o
 * {@link PessoaGateway}, desacoplando-o de detalhes tÃ©cnicos de persistÃªncia.
 *
 * <p>
 * Camada: infrastructure/gateway.
 */
@Component
@RequiredArgsConstructor
public class PessoaGatewayImpl implements PessoaGateway {

    private final PessoaRepository pessoaRepository;
    private final RoleRepository roleRepository;
    private final PessoaEntityMapper pessoaEntityMapper;

    @Override
    public Pessoa salvar(Pessoa pessoa) {
        PessoaEntity entity = pessoaEntityMapper.toEntity(pessoa);
        entity.setRoles(resolverRoles(pessoa.getRoles()));
        return pessoaEntityMapper.toDomain(pessoaRepository.save(entity));
    }

    @Override
    public Optional<Pessoa> buscarPorId(Long id) {
        return pessoaRepository.findById(id).map(pessoaEntityMapper::toDomain);
    }

    @Override
    public Optional<Pessoa> buscarPorEmail(String email) {
        return pessoaRepository.findByEmail(email).map(pessoaEntityMapper::toDomain);
    }

    @Override
    public Page<Pessoa> buscarTodos(String filtro, Pageable pageable) {
        return pessoaRepository.findAll(PessoaSpecification.filtroUnico(filtro), pageable)
                .map(pessoaEntityMapper::toDomain);
    }

    @Override
    public boolean existePorCpf(String cpf) {
        return pessoaRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existePorEmail(String email) {
        return pessoaRepository.existsByEmail(email);
    }

    @Override
    public boolean existePorId(Long id) {
        return pessoaRepository.existsById(id);
    }

    @Override
    public void deletarPorId(Long id) {
        PessoaEntity entity = pessoaRepository.findById(id).orElseThrow(() -> RecursoNaoEncontradoException
                .builder().mensagem("a pessoa com id " + id + " nÃ£o foi encontrada").detalhes("").build());
        entity.setDeletadoEm(LocalDateTime.now());
        pessoaRepository.save(entity);
    }

    private Set<RoleEntity> resolverRoles(Set<RoleEnum> roles) {
        Set<RoleEnum> efetivas = (roles != null && !roles.isEmpty()) ? roles : EnumSet.of(RoleEnum.ROLE_CLIENTE);

        return efetivas.stream()
                .map(roleEnum -> roleRepository.findByNome(roleEnum)
                        .orElseGet(() -> roleRepository.save(RoleEntity.builder().nome(roleEnum).build())))
                .collect(Collectors.toSet());
    }
}
