package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.VeiculoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.veiculo.VeiculoRepository;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeiculoGatewayImpl implements VeiculoGateway {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoEntityMapper veiculoEntityMapper;

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        return veiculoEntityMapper
                .toDomain(veiculoRepository.save(veiculoEntityMapper.toEntity(veiculo)));
    }

    @Override
    public Optional<Veiculo> buscarPorId(Long id) {
        return veiculoRepository.findById(id).map(veiculoEntityMapper::toDomain);
    }

    @Override
    public List<Veiculo> buscarTodos() {
        return veiculoRepository.findAll().stream().map(veiculoEntityMapper::toDomain).toList();
    }

    @Override
    public List<Veiculo> buscarPorPessoaId(Long pessoaId) {
        return veiculoRepository.findByPessoa_Id(pessoaId).stream().map(veiculoEntityMapper::toDomain).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return veiculoRepository.existsById(id);
    }

    @Override
    public void deletarPorId(Long id) {
        VeiculoEntity entity = veiculoRepository.findById(id).orElseThrow(() -> RecursoNaoEncontradoException
                .builder().mensagem("o veÃ­culo com id " + id + " nÃ£o foi encontrado").detalhes("").build());
        entity.setDeletadoEm(LocalDateTime.now());
        veiculoRepository.save(entity);
    }
}
