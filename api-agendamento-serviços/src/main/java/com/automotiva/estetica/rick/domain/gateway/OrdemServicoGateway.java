package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServicoDuracaoResumo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdemServicoGateway {

    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> buscarPorId(Long id);

    Page<OrdemServico> buscarTodos(String filtro, Pageable pageable);

    List<OrdemServico> buscarPorVeiculoPessoaId(Long pessoaId);

    Optional<OrdemServico> buscarPorIdComDetalhes(Long id);

    Page<OrdemServico> buscarTodosParaGestao(Long status, LocalDateTime dataInicio, LocalDateTime dataFim,
            Pageable pageable);

    List<OrdemServicoDuracaoResumo> buscarDuracaoTotalPorOS(LocalDate data);

    boolean existePorVeiculoIdEDataAgendamento(Long veiculoId, LocalDateTime dataAgendamento);
}
