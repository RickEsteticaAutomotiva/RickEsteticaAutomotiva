package com.automotiva.estetica.rick.adapter.out.persistence.servico;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.ServicoPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ServicoRepositoryAdapter implements ServicoRepositoryPort {

    private final ServicoJpaRepository jpaRepository;
    private final ServicoPersistenceMapper mapper;

    @Override
    public Servico salvar(Servico servico) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(servico)));
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Servico> buscarTodos(String filtro, Pageable pageable) {
        return jpaRepository
                .findAll(ServicoSpecification.filtroUnico(filtro), pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<Servico> buscarPorIds(List<Long> ids) {
        return jpaRepository.findByIdIn(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deletarPorId(Long id) {
        ServicoJpaEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("o serviço com id " + id + " não foi encontrado")
                        .detalhes("")
                        .build());
        entity.setDeletadoEm(LocalDateTime.now());
        jpaRepository.save(entity);
    }
}
