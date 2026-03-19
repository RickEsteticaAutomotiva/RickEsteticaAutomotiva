package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.OrdemServicoDuracaoProjection;
import com.automotiva.estetica.rick.application.dto.response.FaturamentoDiarioDto;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDuracaoDto;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdemServicoRepositoryPort {

    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> buscarPorId(Long id);

    Optional<OrdemServico> buscarPorIdComDetalhes(Long id);

    Page<OrdemServico> buscarTodos(String filtro, Pageable pageable);

    List<OrdemServico> buscarPorVeiculoPessoaId(Long pessoaId);

    List<OrdemServicoDuracaoProjection> buscarDuracaoTotalPorOS(LocalDate data);

    boolean existePorVeiculoIdEDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento);

    BigDecimal somarFaturamentoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    Integer buscarQtdOrdensDoMes(LocalDateTime inicio, LocalDateTime fim);

    Integer buscarQtdOrdensConcluidasNoMes(LocalDateTime inicio, LocalDateTime fim);

    BigDecimal calcularTicketMedioDoMes(LocalDateTime inicio, LocalDateTime fim);

    List<FaturamentoDiarioDto> buscarFaturamentoPorDia(LocalDateTime dataInicial);
}
