package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.PageableFactory;
import com.automotiva.estetica.rick.application.dto.request.AdicionarServicosOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarStatusOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarValorServicoOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoGestaoPageRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoClienteResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDetalheResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoServicoResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoVeiculoResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.port.in.CarrinhoUseCase;
import com.automotiva.estetica.rick.application.port.in.OrdemServicoUseCase;
import com.automotiva.estetica.rick.application.port.out.EmailPort;
import com.automotiva.estetica.rick.application.port.out.ItemServicoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoEventPublisherPort;
import com.automotiva.estetica.rick.application.port.out.OrdemServicoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Email;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Status;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.CampoInvalidoException;
import com.automotiva.estetica.rick.domain.exception.IntegracaoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdemServicoService implements OrdemServicoUseCase {

    private final OrdemServicoRepositoryPort ordemServicoRepositoryPort;
    private final ItemServicoRepositoryPort itemServicoRepositoryPort;
    private final ServicoRepositoryPort servicoRepositoryPort;
    private final CarrinhoUseCase carrinhoUseCase;
    private final OrdemServicoEventPublisherPort ordemServicoPublisher;
    private final EmailPort emailPort;

    @Override
    public Page<OrdemServicoResponse> buscarTodos(PageRequest pageRequest) {
        Pageable pageable = PageableFactory.from(pageRequest);
        return ordemServicoRepositoryPort
                .buscarTodos(pageRequest.getFiltro(), pageable)
                .map(
                        ordem -> {
                            OrdemServicoResponse response = toResponse(ordem);
                            response.setServicos(buscarIdsServicoPorOrdem(ordem.getId()));
                            return response;
                        });
    }

    @Override
    @Transactional
    public OrdemServicoResponse criar(OrdemServicoRequest request) {
        if (ordemServicoRepositoryPort.existePorVeiculoIdEDataAgendamento(request.getVeiculo(),
                request.getDataAgendamento())) {
            throw RecursoJaExisteException.builder().mensagem("um agendamento já existe nessa hora e data").detalhes("")
                    .build();
        }

        OrdemServico ordemServico = OrdemServico.builder().dataAgendamento(request.getDataAgendamento())
                .precoMinimo(request.getPrecoMinimo()).veiculo(Veiculo.builder().id(request.getVeiculo()).build())
                .status(Status.builder().id(1L).build()).observacoes(request.getObservacoes()).build();

        try {
            ordemServico = ordemServicoRepositoryPort.salvar(ordemServico);

            OrdemServico ordemComDetalhes = ordemServicoRepositoryPort.buscarPorIdComDetalhes(ordemServico.getId())
                    .orElseThrow();

            ordemServicoPublisher.publicarOrdemServicoCriada(ordemComDetalhes, request);

            criarItensServico(request.getServicos(), ordemServico);
            carrinhoUseCase.limparCarrinhoPessoa(ordemComDetalhes.getVeiculo().getPessoa().getId());

        } catch (com.automotiva.estetica.rick.domain.exception.DomainException e) {
            // Exceções de domínio sobem diretamente — já tratadas pelo
            // GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            log.error("Falha ao criar ordem de serviço: {}", e.getMessage(), e);
            throw IntegracaoException.builder().mensagem("Falha ao criar ordem de serviço").detalhes(e.getMessage())
                    .build();
        }

        OrdemServicoResponse response = toResponse(ordemServico);
        response.setServicos(request.getServicos());
        return response;
    }

    @Override
    public OrdemServicoResponse buscarPorId(Long id) {
        OrdemServico ordemServico = ordemServicoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a ordem de serviço com id " + id + " não foi encontrada").detalhes("").build());

        OrdemServicoResponse response = toResponse(ordemServico);
        response.setServicos(buscarIdsServicoPorOrdem(id));
        return response;
    }

    @Override
    public List<OrdemServicoResponse> buscarPorUsuarioId(Long usuarioId) {
        return ordemServicoRepositoryPort.buscarPorVeiculoPessoaId(usuarioId).stream()
                .map(
                        ordem -> {
                            OrdemServicoResponse response = toResponse(ordem);
                            response.setServicos(buscarIdsServicoPorOrdem(ordem.getId()));
                            return response;
                        })
                .toList();
    }

    @Override
    @Transactional
    public OrdemServicoResponse atualizar(Long id, OrdemServicoRequest request) {
        OrdemServico ordemServico = ordemServicoRepositoryPort.buscarPorIdComDetalhes(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a ordem de serviço com id " + id + " não foi encontrada").detalhes("").build());

        // Aplica atualização via método de domínio
        ordemServico.atualizar(request.getDataAgendamento(), request.getPrecoMinimo(), request.getObservacoes(),
                request.getStatus(), request.getMotivo());

        // Regra de notificação encapsulada no domínio
        if (ordemServico.deveNotificarPorEmail()) {
            enviarEmailAtualizacao(ordemServico);
        }

        ordemServicoRepositoryPort.salvar(ordemServico);

        OrdemServicoResponse response = toResponse(ordemServico);
        response.setServicos(buscarIdsServicoPorOrdem(id));
        return response;
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private void criarItensServico(List<Long> servicoIds, OrdemServico ordemServico) {
        for (Long servicoId : servicoIds) {
            Servico servico = servicoRepositoryPort.buscarPorId(servicoId)
                    .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                            .mensagem("serviço " + servicoId + " não encontrado").detalhes("").build());
            itemServicoRepositoryPort.salvar(ordemServico.criarItem(servico));
        }
    }

    private List<Long> buscarIdsServicoPorOrdem(Long ordemId) {
        return itemServicoRepositoryPort.buscarPorOrdemServicoId(ordemId).stream()
                .map(item -> item.getServico().getId())
                .toList();
    }

    private void enviarEmailAtualizacao(OrdemServico ordemServico) {
        try {
            Email email = new Email();
            email.setAssunto("Atualização de Status da Ordem de Serviço #" + ordemServico.getId());
            email.setCorpo(String.format(
                    "A ordem de serviço #%d teve seu status atualizado para: %s%nCliente: %s%nVeículo: %s%nData: %s",
                    ordemServico.getId(),
                    ordemServico.getStatus().getDescricao(),
                    ordemServico.getVeiculo().getPessoa().getNome(),
                    ordemServico.getVeiculo().getModelo(),
                    ordemServico.getDataAgendamento()));
            email.setDestinatario(ordemServico.getVeiculo().getPessoa().getEmail());
            emailPort.enviarEmailComAnexos(email, null);
        } catch (Exception e) {
            log.warn("Falha ao enviar e-mail de atualização para ordem {}: {}", ordemServico.getId(), e.getMessage());
        }
    }

    private OrdemServicoClienteResumoResponse toClienteResumoPadrao(OrdemServico ordemServico) {
        if (ordemServico.getVeiculo() == null || ordemServico.getVeiculo().getPessoa() == null) {
            return null;
        }
        return OrdemServicoClienteResumoResponse.builder()
                .id(ordemServico.getVeiculo().getPessoa().getId())
                .nome(ordemServico.getVeiculo().getPessoa().getNome())
                .build();
    }

    private OrdemServicoResponse toResponse(OrdemServico o) {
        return OrdemServicoResponse.builder().id(o.getId()).dataAgendamento(o.getDataAgendamento())
                .precoMinimo(o.getPrecoMinimo())
                .veiculo(o.getVeiculo() != null ? o.getVeiculo().getId() : null)
                .status(o.getStatus() != null ? o.getStatus().getId() : null)
                .observacoes(o.getObservacoes())
                .dtConclusao(o.getDtConclusao())
                .motivo(
                        o.getMotivoCancelamento() != null
                                ? o.getMotivoCancelamento().getId()
                                : null)
                .cliente(toClienteResumoPadrao(o))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdemServicoResumoResponse> buscarTodosParaGestao(OrdemServicoGestaoPageRequest request) {
        if (request.getDataInicio() != null
                && request.getDataFim() != null
                && request.getDataInicio().isAfter(request.getDataFim())) {
            throw CampoInvalidoException.builder()
                    .mensagem("dataInicio não pode ser maior que dataFim")
                    .detalhes("")
                    .build();
        }

        PageRequest pageRequest =
                PageRequest.builder()
                        .pagina(request.getPagina())
                        .tamanho(request.getTamanho())
                        .ordenarPor(request.getOrdenarPor())
                        .build();

        Pageable pageable = PageableFactory.from(pageRequest);
        LocalDateTime dataInicio =
                request.getDataInicio() != null ? request.getDataInicio().atStartOfDay() : null;
        LocalDateTime dataFim =
                request.getDataFim() != null ? request.getDataFim().atTime(23, 59, 59) : null;

        return ordemServicoRepositoryPort
                .buscarTodosParaGestao(pageRequest.getFiltro(), request.getStatus(), dataInicio, dataFim, pageable)
                .map(this::toResumoGestao);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdemServicoDetalheResponse buscarDetalheParaGestao(Long ordemServicoId) {
        OrdemServico ordemServico =
                ordemServicoRepositoryPort
                        .buscarPorIdComDetalhes(ordemServicoId)
                        .orElseThrow(
                                () ->
                                        RecursoNaoEncontradoException.builder()
                                                .mensagem(
                                                        "a ordem de serviço com id "
                                                                + ordemServicoId
                                                                + " não foi encontrada")
                                                .detalhes("")
                                                .build());
        return toDetalheGestao(ordemServico);
    }

    @Override
    public OrdemServicoDetalheResponse atualizarStatusParaGestao(
            Long ordemServicoId, AtualizarStatusOrdemRequest request) {
        throw CampoInvalidoException.builder()
                .mensagem("endpoint de gestão não habilitado neste controller")
                .detalhes("")
                .build();
    }

    @Override
    public OrdemServicoDetalheResponse adicionarServicosParaGestao(
            Long ordemServicoId, AdicionarServicosOrdemRequest request) {
        throw CampoInvalidoException.builder()
                .mensagem("endpoint de gestão não habilitado neste controller")
                .detalhes("")
                .build();
    }

    @Override
    public OrdemServicoDetalheResponse atualizarValorServicoParaGestao(
            Long ordemServicoId, Long servicoId, AtualizarValorServicoOrdemRequest request) {
        throw CampoInvalidoException.builder()
                .mensagem("endpoint de gestão não habilitado neste controller")
                .detalhes("")
                .build();
    }

    @Override
    public OrdemServicoDetalheResponse removerServicoParaGestao(Long ordemServicoId, Long servicoId) {
        throw CampoInvalidoException.builder()
                .mensagem("endpoint de gestão não habilitado neste controller")
                .detalhes("")
                .build();
    }

    private OrdemServicoResumoResponse toResumoGestao(OrdemServico ordemServico) {
        List<ItemServico> itens = itemServicoRepositoryPort.buscarPorOrdemServicoId(ordemServico.getId());
        BigDecimal total =
                itens.stream()
                        .map(ItemServico::getPreco)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrdemServicoResumoResponse.builder()
                .id(ordemServico.getId())
                .dataAgendamento(ordemServico.getDataAgendamento())
                .dataConclusao(ordemServico.getDtConclusao())
                .status(ordemServico.getStatus() != null ? ordemServico.getStatus().getId() : null)
                .observacoes(ordemServico.getObservacoes())
                .valorTotal(total)
                .cliente(toClienteResumo(ordemServico))
                .veiculo(toVeiculoResumo(ordemServico))
                .servicos(toServicosResumo(itens))
                .build();
    }

    private OrdemServicoDetalheResponse toDetalheGestao(OrdemServico ordemServico) {
        List<ItemServico> itens = itemServicoRepositoryPort.buscarPorOrdemServicoId(ordemServico.getId());
        BigDecimal total =
                itens.stream()
                        .map(ItemServico::getPreco)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrdemServicoDetalheResponse.builder()
                .id(ordemServico.getId())
                .dataAgendamento(ordemServico.getDataAgendamento())
                .dataConclusao(ordemServico.getDtConclusao())
                .status(ordemServico.getStatus() != null ? ordemServico.getStatus().getId() : null)
                .observacoes(ordemServico.getObservacoes())
                .valorTotal(total)
                .cliente(toClienteResumo(ordemServico))
                .veiculo(toVeiculoResumo(ordemServico))
                .servicos(toServicosResumo(itens))
                .build();
    }

    private OrdemServicoClienteResumoResponse toClienteResumo(OrdemServico ordemServico) {
        if (ordemServico.getVeiculo() == null || ordemServico.getVeiculo().getPessoa() == null) {
            return null;
        }
        return OrdemServicoClienteResumoResponse.builder()
                .id(ordemServico.getVeiculo().getPessoa().getId())
                .nome(ordemServico.getVeiculo().getPessoa().getNome())
                .build();
    }

    private OrdemServicoVeiculoResumoResponse toVeiculoResumo(OrdemServico ordemServico) {
        if (ordemServico.getVeiculo() == null) {
            return null;
        }
        return OrdemServicoVeiculoResumoResponse.builder()
                .id(ordemServico.getVeiculo().getId())
                .marca(ordemServico.getVeiculo().getMarca())
                .modelo(ordemServico.getVeiculo().getModelo())
                .placa(ordemServico.getVeiculo().getPlaca())
                .build();
    }

    private List<OrdemServicoServicoResumoResponse> toServicosResumo(List<ItemServico> itens) {
        return itens.stream()
                .map(
                        item ->
                                OrdemServicoServicoResumoResponse.builder()
                                        .id(item.getServico() != null ? item.getServico().getId() : null)
                                        .nome(item.getServico() != null ? item.getServico().getNome() : null)
                                        .valorAplicado(item.getPreco())
                                        .preco(item.getServico() != null ? item.getServico().getPreco() : null)
                                        .build())
                .toList();
    }
}
