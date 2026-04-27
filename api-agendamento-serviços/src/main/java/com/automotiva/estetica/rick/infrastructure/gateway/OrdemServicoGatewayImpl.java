package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.mapper.OrdemServicoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.ordemservico.OrdemServicoRepository;
import com.automotiva.estetica.rick.infrastructure.repository.ordemservico.OrdemServicoSpecification;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServicoDuracaoResumo;
import com.automotiva.estetica.rick.domain.gateway.OrdemServicoGateway;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdemServicoGatewayImpl implements OrdemServicoGateway {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoEntityMapper ordemServicoEntityMapper;

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        return ordemServicoEntityMapper
                .toDomain(ordemServicoRepository.save(ordemServicoEntityMapper.toEntity(ordemServico)));
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        return ordemServicoRepository.findById(id).map(ordemServicoEntityMapper::toDomain);
    }

    @Override
    public Page<OrdemServico> buscarTodos(String filtro, Pageable pageable) {
        return ordemServicoRepository.findAll(OrdemServicoSpecification.filtroUnico(filtro), pageable)
                .map(ordemServicoEntityMapper::toDomain);
    }

    @Override
    public List<OrdemServico> buscarPorVeiculoPessoaId(Long pessoaId) {
        return ordemServicoRepository.findByVeiculo_Pessoa_Id(pessoaId).stream().map(ordemServicoEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<OrdemServico> buscarPorIdComDetalhes(Long id) {
        return ordemServicoRepository.findOrdemServicoById(id).map(ordemServicoEntityMapper::toDomain);
    }

    @Override
    public Page<OrdemServico> buscarTodosParaGestao(Long status, LocalDateTime dataInicio, LocalDateTime dataFim,
            Pageable pageable) {
        return ordemServicoRepository
                .findAll(OrdemServicoSpecification.filtroGestao(null, status, dataInicio, dataFim), pageable)
                .map(ordemServicoEntityMapper::toDomain);
    }

    @Override
    public List<OrdemServicoDuracaoResumo> buscarDuracaoTotalPorOS(LocalDate data) {
        return ordemServicoRepository.buscarDuracaoTotalPorOS(data).stream().map(
                item -> new OrdemServicoDuracaoResumo(item.getId(), item.getDataAgendamento(), item.getDuracaoTotal()))
                .toList();
    }

    @Override
    public boolean existePorVeiculoIdEDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento) {
        return ordemServicoRepository.existsByVeiculoIdAndDataAgendamento(veiculoId, dataAgendamento);
    }

    @Override
    public List<OrdemServico> buscarAgendamentosDodia(LocalDate data) {
        return ordemServicoRepository.buscarAgendamentosDodia(data).stream()
                .map(ordemServicoEntityMapper::toDomain)
                .toList();
    }
}
