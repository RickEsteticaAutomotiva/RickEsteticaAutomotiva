package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.OrdemServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CalendarEventRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.page_request.DefaultPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.observer.EmailObserver;
import com.automotiva.estetica.rick.api_agendamento_servicos.service.observer.OrdemServicoSubject;
import com.automotiva.estetica.rick.api_agendamento_servicos.specification.OrdemServicoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdemServicoService extends OrdemServicoSubject {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;
    private final EmailService emailService;
    private final ItemServicoService itemServicoService;
    private final CalendarioGoogleService calendarioGoogleService;
    private final ServicoRepository servicoRepository;
    private final VeiculoService veiculoService;
    private final CarrinhoService carrinhoService;

    public Page<OrdemServicoDto> buscarTodos(DefaultPageRequest ordemServicoPageRequest) {
        String ordenarPor = ordemServicoPageRequest.getOrdenarPor();

        if (ordenarPor == null || ordenarPor.isBlank()) {
            ordenarPor = "id";
        }

        String[] camposOrdenacao = ordenarPor.split(",");
        for (int i = 0; i < camposOrdenacao.length; i++) {
            camposOrdenacao[i] = camposOrdenacao[i].trim();
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                ordemServicoPageRequest.getPagina(),
                ordemServicoPageRequest.getTamanho(),
                org.springframework.data.domain.Sort.by(camposOrdenacao)
        );

        Specification<OrdemServicoEntity> spec = OrdemServicoSpecification.filtroUnico(ordemServicoPageRequest.getFiltro());
        Page<OrdemServicoEntity> paginaOrdemServico = ordemServicoRepository.findAll(spec, pageable);
        return paginaOrdemServico.map(ordemServicoMapper::ordemServicoParaOrdemServicoDto);
    }

    public OrdemServicoDto criarOrdemServico(OrdemServicoDto ordemServico) {
        if (ordemServicoRepository.existsByVeiculoIdAndDataAgendamento(
                ordemServico.getVeiculo(),
                ordemServico.getDataAgendamento()
        )) {
            throw RecursoJaExisteException.builder()
                    .mensagem("um agendamento já existe nessa hora e data")
                    .detalhes("")
                    .build();
        }
        ordemServico.setStatus(1L);
        OrdemServicoEntity ordemServicoEntity = ordemServicoMapper.ordemServicoDtoParaOrdemServico(ordemServico);

        try {
            ordemServicoEntity = ordemServicoRepository.save(ordemServicoEntity);
            CalendarEventRequest eventRequest = montarEventoGoogleCalendar(ordemServicoEntity, ordemServico);
            calendarioGoogleService.criarEvento(eventRequest);
            itemServicoService.criarItemServico(ordemServico, ordemServicoEntity);
        } catch (Exception e) {
            log.info("falha ao criar ordem de serviço");
            throw new RuntimeException();
        };

        OrdemServicoDto retorno = ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServicoEntity);
        retorno.setServicos(ordemServico.getServicos());
        return retorno;
    }

    public OrdemServicoDto buscarPorId(Long id) {
        Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);

        if (ordemServico.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a ordem de serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }

        OrdemServicoDto retorno = ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServico.get());
        retorno.setServicos(itemServicoService.buscarServicosPorOrdemServicoId(retorno.getId()));
        return retorno;
    }

    public List<OrdemServicoDto> buscarPorUsuarioId(Long id) {
        List<OrdemServicoEntity> ordensServico = ordemServicoRepository.findByVeiculo_Pessoa_Id(id);

        if (ordensServico.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("ordens de serviço do usuário " + id + " não foram encontradas")
                    .detalhes("")
                    .build();
        }

        List<OrdemServicoDto> ordensDoUsuario = ordensServico.stream()
                .map(ordem -> {
                    OrdemServicoDto retorno = ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordem);
                    retorno.setServicos(itemServicoService.buscarServicosPorOrdemServicoId(retorno.getId()));
                    return retorno;
                })
                .toList();

        return ordensDoUsuario;
    }

    public OrdemServicoDto atualizarOrdemServico(Long id, OrdemServicoDto ordemServicoAtualizada) {
        EmailObserver emailObserver = new EmailObserver(emailService);
        subscribe(emailObserver);
        Optional<OrdemServicoEntity> ordemServicoExistente = ordemServicoRepository.findOrdemServicoEntityById(id);

        if (ordemServicoExistente.isEmpty()) {
            unsubscribe(emailObserver);
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a ordem de serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }

        OrdemServicoEntity ordemServico = ordemServicoExistente.get();

        if (ordemServicoAtualizada.getStatus() == 2L || ordemServicoAtualizada.getStatus() == 5L){
            notifyObservers(ordemServico);
        }else {
            unsubscribe(emailObserver);
        }

        ordemServicoMapper.atualizarOrdemServicoEntityFromDto(ordemServicoAtualizada, ordemServico);
        ordemServicoRepository.save(ordemServico);

        OrdemServicoDto retorno = ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServico);
        retorno.setServicos(itemServicoService.buscarServicosPorOrdemServicoId(retorno.getId()));
        return retorno;
    }

    public void deletarOrdemServico(Long id) {
        Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);
        if (ordemServico.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a ordem de serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }
        ordemServicoRepository.deleteById(id);
    }

    private CalendarEventRequest montarEventoGoogleCalendar(
            OrdemServicoEntity ordemServico,
            OrdemServicoDto ordemServicoServicos
    ) {
        CalendarEventRequest request = new CalendarEventRequest();
        ZonedDateTime zoned = ordemServico.getDataAgendamento().atZone(ZoneId.of("America/Sao_Paulo"));
        OrdemServicoEntity ordem = ordemServicoRepository.findOrdemServicoEntityById(ordemServico.getId()).get();

        carrinhoService.limparCarrinhoPessoa(ordem.getVeiculo().getPessoa().getId());


        request.setTitulo("Atendimento veículo - " + ordem.getVeiculo().getPlaca());
        request.setDescricao(montarDescricaoEvento(ordemServico, ordemServicoServicos));
        request.setLocalizacao("Estética Automotiva Rick - Av. Principal, 123");
        request.setDataHoraInicio(zoned.toInstant());
        request.setDataHoraFim(zoned.plusHours(1).toInstant());
        request.setFusoHorario("America/Sao_Paulo");
        request.setEmailsParticipantes(List.of());
        request.setVisibilidade("default");
        request.setConvidadosPodemVerOutrosConvidados(false);
        request.setConvidadosPodemConvidarOutros(false);
        request.setTransparencia("opaque");

        return request;
    }

    private String montarDescricaoEvento(OrdemServicoEntity ordemServico, OrdemServicoDto ordemServicos) {
        StringBuilder descricao = new StringBuilder();
        List<ServicoEntity> servicos = servicoRepository.findByIdIn(ordemServicos.getServicos());


        descricao.append("Data: ").append(ordemServico.getDataAgendamento()).append("\n\n");
        descricao.append("Serviços:\n");

        servicos.forEach(servico ->
                descricao.append("- ").append(servico.getNome()).append("\n")
        );

        if (ordemServico.getObservacoes() != null && !ordemServico.getObservacoes().isBlank()) {
            descricao.append("\nObservações: ").append(ordemServico.getObservacoes());
        }

        return descricao.toString();
    }

}