package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.assembler.OrdemServicoResponseAssembler;
import com.automotiva.estetica.rick.application.PageableFactory;
import com.automotiva.estetica.rick.application.dto.request.AdicionarServicosOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarOrdemServicoGestaoRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarValorServicoOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoGestaoPageRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.ServicoAplicadoRequest;
import com.automotiva.estetica.rick.application.dto.response.*;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.exception.IntegracaoException;
import com.automotiva.estetica.rick.domain.gateway.ItemServicoGateway;
import com.automotiva.estetica.rick.domain.usecase.AdicionarServicosOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarStatusOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.AtualizarValorItemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdemServicoComDetalhesUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdemServicoPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarHorariosDisponiveisUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdensServicoPorUsuarioUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarOrdensServicoParaGestaoUseCase;
import com.automotiva.estetica.rick.domain.usecase.CriarOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.LimparCarrinhoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarOrdensServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.NotificarAtualizacaoOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.RemoverServicoOrdemServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarAgendamentosHojeUseCase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import com.automotiva.estetica.rick.application.dto.request.CancelarOrdemRequest;
import com.automotiva.estetica.rick.domain.enums.StatusOrdem;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdemServicoApplicationService {

    private static final ZoneId ZONE_ID_SAO_PAULO = ZoneId.of("America/Sao_Paulo");

    private final ItemServicoGateway itemServicoGateway;
    private final LimparCarrinhoPessoaUseCase limparCarrinhoPessoaUseCase;
    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase;
    private final AtualizarStatusOrdemServicoUseCase atualizarStatusOrdemServicoUseCase;
    private final AdicionarServicosOrdemServicoUseCase adicionarServicosOrdemServicoUseCase;
    private final AtualizarValorItemServicoUseCase atualizarValorItemServicoUseCase;
    private final RemoverServicoOrdemServicoUseCase removerServicoOrdemServicoUseCase;
    private final BuscarOrdensServicoParaGestaoUseCase buscarOrdensServicoParaGestaoUseCase;
    private final BuscarOrdemServicoComDetalhesUseCase buscarOrdemServicoComDetalhesUseCase;
    private final ListarOrdensServicoUseCase listarOrdensServicoUseCase;
    private final BuscarOrdemServicoPorIdUseCase buscarOrdemServicoPorIdUseCase;
    private final BuscarOrdensServicoPorUsuarioUseCase buscarOrdensServicoPorUsuarioUseCase;
    private final BuscarHorariosDisponiveisUseCase buscarHorariosDisponiveisUseCase;
    private final NotificarAtualizacaoOrdemServicoUseCase notificarAtualizacaoOrdemServicoUseCase;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;
    private final BuscarAgendamentosHojeUseCase buscarAgendamentosHojeUseCase;

    public Page<OrdemServicoResponse> buscarTodos(PageRequest pageRequest) {
        Pageable pageable = PageableFactory.from(pageRequest);
        return listarOrdensServicoUseCase.execute(pageRequest.getFiltro(), pageable)
                .map(ordem -> ordemServicoResponseAssembler
                        .toResponse(ordem, buscarItensPorOrdem(ordem.getId())));
    }

    @Transactional
    public OrdemServicoResponse criar(OrdemServicoRequest request) {
        OrdemServico ordemServico;

        try {
            ordemServico = criarOrdemServicoUseCase.execute(request.getDataAgendamento(), request.getPrecoMinimo(),
                    request.getVeiculo(), request.getObservacoes(), request.getServicos());

            if (ordemServico.getVeiculo() != null && ordemServico.getVeiculo().getPessoa() != null
                    && ordemServico.getVeiculo().getPessoa().getId() != null) {
                limparCarrinhoPessoaUseCase.execute(ordemServico.getVeiculo().getPessoa().getId());
            }

        } catch (com.automotiva.estetica.rick.domain.exception.DomainException e) {
            // Exceções de domínio sobem diretamente — já tratadas pelo
            // GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            log.error("Falha ao criar ordem de serviço: {}", e.getMessage(), e);
            throw IntegracaoException.builder().mensagem("Falha ao criar ordem de serviço").detalhes(e.getMessage())
                    .build();
        }

        return ordemServicoResponseAssembler.toResponse(ordemServico, buscarItensPorOrdem(ordemServico.getId()));
    }

    @Transactional
    public OrdemServicoDetalheResponse criarParaGestao(OrdemServicoRequest request) {
        OrdemServico ordemServico = criarOrdemServicoUseCase.execute(request.getDataAgendamento(),
                request.getPrecoMinimo(), request.getVeiculo(), request.getObservacoes(), request.getServicos());
        return ordemServicoResponseAssembler.toDetalheGestao(ordemServico, buscarItensPorOrdem(ordemServico.getId()));
    }

    public OrdemServicoResponse buscarPorId(Long id) {
        OrdemServico ordemServico = buscarOrdemServicoPorIdUseCase.execute(id);
        return ordemServicoResponseAssembler.toResponse(ordemServico, buscarItensPorOrdem(id));
    }

    public List<OrdemServicoResponse> buscarPorUsuarioId(Long usuarioId) {
        return buscarOrdensServicoPorUsuarioUseCase.execute(usuarioId).stream()
                .map(ordem -> ordemServicoResponseAssembler.toResponse(ordem, buscarItensPorOrdem(ordem.getId())))
                .toList();
    }

    public List<HorarioDisponivelResponse> buscarHorariosDisponiveis(LocalDate data, List<Long> servicosIds) {
        return buscarHorariosDisponiveisUseCase.execute(data, servicosIds).stream()
                .map(horario -> new HorarioDisponivelResponse(horario.inicio(), horario.fim())).toList();
    }

    @Transactional(readOnly = true)
    public AgendamentosHojeListResponse buscarAgendamentosHoje() {
        LocalDate hoje = LocalDate.now(ZONE_ID_SAO_PAULO);
        List<OrdemServico> agendamentos = buscarAgendamentosHoje(hoje);

        List<AgendamentoHojeResponse> agendamentosResponse = agendamentos.stream()
                .map(ordem -> ordemServicoResponseAssembler.toAgendamentoHojeResponse(ordem,
                        buscarItensPorOrdem(ordem.getId())))
                .toList();

        return AgendamentosHojeListResponse.builder().data(agendamentosResponse).total(agendamentosResponse.size())
                .timestamp(LocalDateTime.now(ZONE_ID_SAO_PAULO)).build();
    }

    @Transactional
    public OrdemServicoResponse atualizar(Long id, OrdemServicoRequest request) {
        OrdemServico ordemServico = atualizarOrdemServicoUseCase.execute(id, request.getDataAgendamento(),
                request.getPrecoMinimo(), request.getObservacoes(), request.getStatus(), request.getMotivo());

        // Regra de notificação encapsulada no domínio
        notificarAtualizacaoOrdemServicoUseCase.execute(ordemServico);

        return ordemServicoResponseAssembler.toResponse(ordemServico, buscarItensPorOrdem(id));
    }

    @Transactional(readOnly = true)
    public Page<OrdemServicoResumoResponse> buscarTodosParaGestao(OrdemServicoGestaoPageRequest request) {
        Pageable pageable = PageableFactory.from(request);
        return buscarOrdensServicoParaGestaoUseCase
                .execute(request.getStatus(), request.getDataInicio(), request.getDataFim(), pageable)
                .map(ordem -> ordemServicoResponseAssembler.toResumoGestao(ordem, buscarItensPorOrdem(ordem.getId())));
    }

    @Transactional(readOnly = true)
    public OrdemServicoDetalheResponse buscarDetalheParaGestao(Long ordemServicoId) {
        OrdemServico ordemServico = buscarOrdemServicoComDetalhesUseCase.execute(ordemServicoId);
        return ordemServicoResponseAssembler.toDetalheGestao(ordemServico, buscarItensPorOrdem(ordemServicoId));
    }

    @Transactional
    public OrdemServicoDetalheResponse atualizarParaGestao(Long ordemServicoId,
            AtualizarOrdemServicoGestaoRequest request) {
        LocalDateTime dataAgendamento = request != null ? request.getDataAgendamento() : null;
        String observacoes = request != null && request.getObservacoes() != null
                ? request.getObservacoes().trim()
                : null;
        Long statusId = request != null ? request.getStatus() : null;

        OrdemServico ordemServico = atualizarOrdemServicoUseCase.execute(ordemServicoId, dataAgendamento, null,
                observacoes, statusId, null);

        notificarAtualizacaoOrdemServicoUseCase.execute(ordemServico);

        OrdemServico ordemComDetalhes = buscarOrdemPorIdComDetalhes(ordemServicoId);
        return ordemServicoResponseAssembler.toDetalheGestao(ordemComDetalhes, buscarItensPorOrdem(ordemServicoId));
    }

    @Transactional
    public OrdemServicoDetalheResponse cancelarParaGestao(Long ordemServicoId, CancelarOrdemRequest request) {
        if (request == null || request.getMotivo() == null) {
            throw CampoInvalidoException.builder().mensagem("motivo é obrigatório").detalhes("").build();
        }

        // Buscar ordem com detalhes para validar estado atual
        OrdemServico ordemServico = buscarOrdemPorIdComDetalhes(ordemServicoId);
        Long statusAtual = ordemServico.getStatus() != null ? ordemServico.getStatus().getId() : null;

        // Não permitir cancelar se já estiver concluída ou já cancelada
        if (StatusOrdem.CONCLUIDO.getId().equals(statusAtual) || StatusOrdem.CANCELADO.getId().equals(statusAtual)) {
            throw CampoInvalidoException.builder().mensagem("ordem de serviço não pode ser cancelada no estado atual")
                    .detalhes("")
                    .build();
        }

        // Atualiza status para CANCELADO e persiste motivo
        OrdemServico ordemAtualizada = atualizarOrdemServicoUseCase.execute(ordemServicoId, null, null, null,
                StatusOrdem.CANCELADO.getId(), request.getMotivo());

        // Regra de notificação encapsulada no domínio / use case de notificação
        notificarAtualizacaoOrdemServicoUseCase.execute(ordemAtualizada);

        OrdemServico ordemComDetalhes = buscarOrdemPorIdComDetalhes(ordemServicoId);
        return ordemServicoResponseAssembler.toDetalheGestao(ordemComDetalhes, buscarItensPorOrdem(ordemServicoId));
    }

    @Transactional
    public OrdemServicoDetalheResponse adicionarServicosParaGestao(Long ordemServicoId,
            AdicionarServicosOrdemRequest request) {
        List<Long> servicoIds = request != null && request.getServicos() != null
                ? request.getServicos().stream().map(ServicoAplicadoRequest::getIdServico).toList()
                : List.of();
        List<BigDecimal> valoresAplicados = request != null && request.getServicos() != null
                ? request.getServicos().stream().map(ServicoAplicadoRequest::getValorAplicado).toList()
                : List.of();

        adicionarServicosOrdemServicoUseCase.execute(ordemServicoId, servicoIds, valoresAplicados);
        OrdemServico ordemComDetalhes = buscarOrdemPorIdComDetalhes(ordemServicoId);
        return ordemServicoResponseAssembler.toDetalheGestao(ordemComDetalhes, buscarItensPorOrdem(ordemServicoId));
    }

    @Transactional
    public OrdemServicoDetalheResponse atualizarValorServicoParaGestao(Long ordemServicoId, Long servicoId,
            AtualizarValorServicoOrdemRequest request) {
        BigDecimal valorAplicado = request != null ? request.getValorAplicado() : null;
        atualizarValorItemServicoUseCase.execute(ordemServicoId, servicoId, valorAplicado);
        OrdemServico ordemComDetalhes = buscarOrdemPorIdComDetalhes(ordemServicoId);
        return ordemServicoResponseAssembler.toDetalheGestao(ordemComDetalhes, buscarItensPorOrdem(ordemServicoId));
    }

    @Transactional
    public OrdemServicoDetalheResponse removerServicoParaGestao(Long ordemServicoId, Long servicoId) {
        removerServicoOrdemServicoUseCase.execute(ordemServicoId, servicoId);
        OrdemServico ordemComDetalhes = buscarOrdemPorIdComDetalhes(ordemServicoId);
        return ordemServicoResponseAssembler.toDetalheGestao(ordemComDetalhes, buscarItensPorOrdem(ordemServicoId));
    }

    private OrdemServico buscarOrdemPorIdComDetalhes(Long ordemServicoId) {
        return buscarOrdemServicoComDetalhesUseCase.execute(ordemServicoId);
    }

    private List<ItemServico> buscarItensPorOrdem(Long ordemId) {
        return itemServicoGateway.buscarPorOrdemServicoId(ordemId);
    }

    public List<OrdemServico> buscarAgendamentosHoje(LocalDate data) {
        return buscarAgendamentosHojeUseCase.execute(data);
    }
}
