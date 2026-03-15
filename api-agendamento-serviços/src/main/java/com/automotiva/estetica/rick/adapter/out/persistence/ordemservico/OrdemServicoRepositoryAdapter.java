package com.automotiva.estetica.rick.adapter.out.persistence.ordemservico;

import com.automotiva.estetica.rick.adapter.out.persistence.mapper.OrdemServicoPersistenceMapper;
import com.automotiva.estetica.rick.application.dto.response.CancelamentoMotivoDto;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoDiarioDto;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoServicoDto;
import com.automotiva.estetica.rick.application.dto.response.ProximoAgendamentoDto;
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
    public boolean existePorVeiculoIdEDataAgendamento(
            Long veiculoId, LocalDateTime dataAgendamento) {
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
                .map(
                        row ->
                                new FaturamentoDiarioDto(
                                        ((java.sql.Date) row[0]).toLocalDate(),
                                        (BigDecimal) row[1]))
                .toList();
    }

    @Override
    public List<FaturamentoServicoDto> buscarFaturamentoServicos(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.buscarFaturamentoServicos(inicio, fim).stream()
                .map(
                        row ->
                                new FaturamentoServicoDto(
                                        ((Number) row[0]).longValue(),
                                        (String) row[1],
                                        ((Number) row[2]).longValue(),
                                        (String) row[3],
                                        ((Number) row[4]).longValue(),
                                        (BigDecimal) row[5]))
                .toList();
    }

    @Override
    public BigDecimal somarReceitaRecebidaDoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.somarReceitaRecebidaDoPeriodo(inicio, fim);
    }

    @Override
    public BigDecimal somarCustoRealizadoDoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.somarCustoRealizadoDoPeriodo(inicio, fim);
    }

    @Override
    public List<CancelamentoMotivoDto> buscarCancelamentosPorMotivoDoPeriodo(
            LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.buscarCancelamentosPorMotivoDoPeriodo(inicio, fim).stream()
                .map(
                        row ->
                                new CancelamentoMotivoDto(
                                        (String) row[0],
                                        ((Number) row[1]).longValue()))
                .toList();
    }

    @Override
    public long contarAgendamentosNoPeriodoExcetoStatus(
            LocalDateTime inicio, LocalDateTime fim, Long statusIdIgnorado) {
        return jpaRepository.contarAgendamentosNoPeriodoExcetoStatus(inicio, fim, statusIdIgnorado);
    }

    @Override
    public BigDecimal somarFaturamentoEstimadoNoPeriodoExcetoStatus(
            LocalDateTime inicio, LocalDateTime fim, Long statusIdIgnorado) {
        return jpaRepository.somarFaturamentoEstimadoNoPeriodoExcetoStatus(
                inicio, fim, statusIdIgnorado);
    }

    @Override
    public Optional<ProximoAgendamentoDto> buscarProximoAgendamentoNoPeriodoExcetoStatus(
            LocalDateTime inicio, LocalDateTime fim, Long statusIdIgnorado) {
        return jpaRepository
                .findFirstByDataAgendamentoBetweenAndStatus_IdNotOrderByDataAgendamentoAscIdAsc(
                        inicio, fim, statusIdIgnorado)
                .map(
                        ordem -> {
                            String servicoPrincipal =
                                    jpaRepository.buscarNomesServicosDaOrdem(ordem.getId()).stream()
                                            .findFirst()
                                            .orElse("");

                            return new ProximoAgendamentoDto(
                                    ordem.getId(),
                                    servicoPrincipal,
                                    ordem.getDataAgendamento(),
                                    ordem.getVeiculo() != null && ordem.getVeiculo().getPessoa() != null
                                            ? ordem.getVeiculo().getPessoa().getNome()
                                            : "",
                                    ordem.getVeiculo() != null ? ordem.getVeiculo().getMarca() : null,
                                    ordem.getVeiculo() != null ? ordem.getVeiculo().getModelo() : null,
                                    ordem.getVeiculo() != null ? ordem.getVeiculo().getPlaca() : null,
                                    ordem.getStatus() != null ? ordem.getStatus().getId() : null);
                        });
    }

    @Override
    public Page<OrdemServico> buscarTodosParaGestao(
            String filtro,
            Long status,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable) {
        return jpaRepository
                .findAll(
                        OrdemServicoSpecification.filtroGestao(
                                filtro, status, dataInicio, dataFim),
                        pageable)
                .map(mapper::toDomain);
    }
}
