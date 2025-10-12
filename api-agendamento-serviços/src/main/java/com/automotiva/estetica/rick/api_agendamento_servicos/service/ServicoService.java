package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.ServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.specification.ServicoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public Page<ServicoDto> buscarTodos(ServicoPageRequest servicoPageRequest) {
        String ordenarPor = servicoPageRequest.getOrdenarPor();

        if (ordenarPor == null || ordenarPor.isBlank()) {
            ordenarPor = "id";
        }

        String[] camposOrdenacao = ordenarPor.split(",");
        for (int i = 0; i < camposOrdenacao.length; i++) {
            camposOrdenacao[i] = camposOrdenacao[i].trim();
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                servicoPageRequest.getPagina(),
                servicoPageRequest.getTamanho(),
                org.springframework.data.domain.Sort.by(camposOrdenacao)
        );

        Specification<ServicoEntity> spec = ServicoSpecification.filtroUnico(servicoPageRequest.getFiltro());
        Page<ServicoEntity> paginaServicos = servicoRepository.findAll(spec, pageable);
        return paginaServicos.map(servicoMapper::servicoParaServicoDto);
    }

    public ServicoDto criarServico(ServicoDto servico) {
        if (servicoRepository.existsById(servico.getId())) {
            throw new RecursoJaExisteException("Serviço");
        }
        ServicoEntity servicoEntity = servicoMapper.servicoDtoParaServico(servico);
        servicoRepository.save(servicoEntity);
        return servicoMapper.servicoParaServicoDto(servicoEntity);
    }

    public ServicoDto buscarPorId(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw new RecursoNaoEncontradaException("Serviço");
        }
        return servicoMapper.servicoParaServicoDto(servico.get());
    }

    public ServicoDto atualizarServico(Long id, ServicoDto servicoAtualizada) {
        Optional<ServicoEntity> servicoExistente = servicoRepository.findById(id);
        if (servicoExistente.isEmpty()) {
            throw new RecursoNaoEncontradaException("Serviço");
        }
        ServicoEntity servico = servicoExistente.get();
        servicoMapper.atualizarServicoEntityFromDto(servicoAtualizada, servico);
        servicoRepository.save(servico);
        return servicoMapper.servicoParaServicoDto(servico);
    }

    public void deletarServico(Long id) {
        Optional<ServicoEntity> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw new RecursoNaoEncontradaException("Serviço");
        }
        servicoRepository.deleteById(id);
    }
}