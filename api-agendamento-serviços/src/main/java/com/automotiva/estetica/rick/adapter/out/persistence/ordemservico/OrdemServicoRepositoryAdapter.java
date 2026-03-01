package com.automotiva.estetica.rick.adapter.out.persistence.ordemservico;

import com.automotiva.estetica.rick.adapter.out.persistence.mapper.OrdemServicoPersistenceMapper;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoDiarioDto;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryAdapter implements OrdemServicoRepositoryPort {

    private final OrdemServicoJpaRepository jpaRepository;
    private final OrdemServicoPersistenceMapper mapper;

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(ordemServico)));
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<OrdemServico> buscarPorIdComDetalhes(Long id) {
        return jpaRepository.findOrdemServicoById(id).map(mapper::toDomain);
    }

    @Override
    public Page<OrdemServico> buscarTodos(String filtro, Pageable pageable) {
        return jpaRepository
                .findAll(OrdemServicoSpecification.filtroUnico(filtro), pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<OrdemServico> buscarPorVeiculoPessoaId(Long pessoaId) {
        return jpaRepository.findByVeiculo_Pessoa_Id(pessoaId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorVeiculoIdEDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento) {
        return jpaRepository.existsByVeiculoIdAndDataAgendamento(veiculoId, dataAgendamento);
    }


    @Override
    public BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.somarFaturamentoDoPeriodo(inicio, fim);
    }

    @Override
    public Integer buscarQtdOrdensDoMes(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.buscarQtdOrdensDoMes(inicio, fim);
    }

    @Override
    public Integer buscarQtdOrdensConcluidasNoMes(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.buscarQtdOrdensConcluidasNoMes(inicio, fim);
    }

    @Override
    public BigDecimal calcularTicketMedioDoMes(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.calcularTicketMedioDoMes(inicio, fim);
    }

    @Override
    public List<FaturamentoDiarioDto> buscarFaturamentoPorDia(LocalDateTime dataInicial) {
        return jpaRepository.buscarFaturamentoPorDia(dataInicial).stream()
                .map(row -> new FaturamentoDiarioDto(((java.sql.Date) row[0]).toLocalDate(), (BigDecimal) row[1]))
                .toList();
    }
}
