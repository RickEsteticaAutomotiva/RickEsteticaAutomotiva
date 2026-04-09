package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.ServicoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.servico.ServicoRepository;
import com.automotiva.estetica.rick.infrastructure.repository.servico.ServicoSpecification;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicoGatewayImpl implements ServicoGateway {

    private final ServicoRepository servicoRepository;
    private final ServicoEntityMapper servicoEntityMapper;

    @Override
    public Servico salvar(Servico servico) {
        ServicoEntity entity = servicoEntityMapper.toEntity(servico);
        return servicoEntityMapper.toDomain(servicoRepository.save(entity));
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepository.findById(id).map(servicoEntityMapper::toDomain);
    }

    @Override
    public Page<Servico> buscarTodos(String filtro, Pageable pageable) {
        return servicoRepository.findAll(ServicoSpecification.filtroUnico(filtro), pageable)
                .map(servicoEntityMapper::toDomain);
    }

    @Override
    public List<Servico> buscarPorIds(List<Long> ids) {
        return servicoRepository.findByIdIn(ids).stream().map(servicoEntityMapper::toDomain).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return servicoRepository.existsById(id);
    }

    @Override
    public void deletarPorId(Long id) {
        ServicoEntity entity = servicoRepository.findById(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("o serviÃ§o com id " + id + " nÃ£o foi encontrado").detalhes("").build());
        entity.setDeletadoEm(LocalDateTime.now());
        servicoRepository.save(entity);
    }
}
