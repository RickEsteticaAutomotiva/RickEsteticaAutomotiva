package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.OrdemServicoMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoPageRequest;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import com.automotiva.estetica.rick.api_agendamento_servicos.specification.OrdemServicoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public Page<OrdemServicoDto> buscarTodos(OrdemServicoPageRequest ordemServicoPageRequest) {
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
        OrdemServicoEntity ordemServicoEntity = ordemServicoMapper.ordemServicoDtoParaOrdemServico(ordemServico);
        ordemServicoRepository.save(ordemServicoEntity);
        return ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServicoEntity);
    }

    public OrdemServicoDto buscarPorId(Long id) {
        Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);
        if (ordemServico.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a ordem de serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }
        return ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServico.get());
    }

    public OrdemServicoDto atualizarOrdemServico(Long id, OrdemServicoDto ordemServicoAtualizada) {
        Optional<OrdemServicoEntity> ordemServicoExistente = ordemServicoRepository.findById(id);
        if (ordemServicoExistente.isEmpty()) {
            throw RecursoNaoEncontradaException.builder()
                    .mensagem("a ordem de serviço com id " + id + " não foi encontrado")
                    .detalhes("")
                    .build();
        }
        OrdemServicoEntity ordemServico = ordemServicoExistente.get();
        ordemServicoMapper.atualizarOrdemServicoEntityFromDto(ordemServicoAtualizada, ordemServico);
        ordemServicoRepository.save(ordemServico);
        return ordemServicoMapper.ordemServicoParaOrdemServicoDto(ordemServico);
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
}