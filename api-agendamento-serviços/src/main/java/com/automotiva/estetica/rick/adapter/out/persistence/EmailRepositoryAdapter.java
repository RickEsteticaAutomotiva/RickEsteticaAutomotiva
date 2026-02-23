package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.mapper.EmailPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.EmailRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailRepositoryAdapter implements EmailRepositoryPort {

    private final EmailJpaRepository jpaRepository;
    private final EmailPersistenceMapper mapper;

    @Override
    public Email salvar(Email email) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(email)));
    }
}
