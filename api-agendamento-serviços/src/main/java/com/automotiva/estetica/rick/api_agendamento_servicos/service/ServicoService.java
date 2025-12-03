package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.page_request.DefaultPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.specification.ServicoSpecification;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public Page<ServicoDto> buscarTodos(DefaultPageRequest pageRequest) {
        String ordenarPor = pageRequest.getOrdenarPor();

        if (ordenarPor == null || ordenarPor.isBlank()) {
            ordenarPor = "id";
        }

        String[] camposOrdenacao = ordenarPor.split(",");
        for (int i = 0; i < camposOrdenacao.length; i++) {
            camposOrdenacao[i] = camposOrdenacao[i].trim();
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pageRequest.getPagina(),
                pageRequest.getTamanho(),
                org.springframework.data.domain.Sort.by(camposOrdenacao));

        Specification<ServicoEntity> spec = ServicoSpecification.filtroUnico(pageRequest.getFiltro());
        Page<ServicoEntity> paginaServicos = servicoRepository.findAll(spec, pageable);
        return paginaServicos.map(servicoMapper::servicoParaServicoDto);
    }

    public ServicoDto criarServico(ServicoDto servico) {
        if (servicoRepository.existsById(servico.getId())) {
            throw RecursoJaExisteException.builder()
                    .mensagem("o serviço já existe no sistema")
                    .detalhes("")
                    .build();
        }
        ServicoEntity servicoEntity = servicoMapper.servicoDtoParaServico(servico);
        servicoRepository.save(servicoEntity);
        return servicoMapper.servicoParaServicoDto(servicoEntity);
    }

    public ServicoDto buscarPorId(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);

        if (servico.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("o serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }

        return servicoMapper.servicoParaServicoDto(servico.get());
    }

    public ServicoDto atualizarServico(Long id, ServicoDto servicoAtualizada) {
        Optional<ServicoEntity> servicoExistente = servicoRepository.findById(id);

        if (servicoExistente.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("o serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }

        ServicoEntity servico = servicoExistente.get();
        servicoMapper.atualizarServicoEntityFromDto(servicoAtualizada, servico);
        servicoRepository.save(servico);
        return servicoMapper.servicoParaServicoDto(servico);
    }

    public void deletarServico(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("o serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }
        servicoRepository.deleteById(id);
    }
}
