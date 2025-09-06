package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoSemObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.OrdemServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {
    @Autowired
    OrdemServicoRepository ordemServicoRepository;

    public RetornoComListaObjeto<OrdemServicoEntity> buscarTodos() {

        try {
            List<OrdemServicoEntity> ordemServicos = ordemServicoRepository.findAll();

            if (ordemServicos.isEmpty()) {
                return RetornoComListaObjeto.<OrdemServicoEntity>builder()
                        .statusCode(404)
                        .mensagem("Nenhuma ordem de serviço encontrada.")
                        .objeto(List.of())
                        .build();
            }

            return RetornoComListaObjeto.<OrdemServicoEntity>builder()
                    .statusCode(200)
                    .mensagem("Ordem de serviço encontradas com sucesso.")
                    .objeto(ordemServicos)
                    .build();

        } catch (Exception e) {
            return RetornoComListaObjeto.<OrdemServicoEntity>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar ordemServicos: " + e.getMessage())
                    .objeto(List.of())
                    .build();
        }
    }

    public RetornoSemObjeto criarOrdemServico(OrdemServicoDto ordemServico) {
        try {

            if (ordemServicoRepository.existsById(ordemServico.getId())) {
                return RetornoSemObjeto.builder()
                        .statusCode(400)
                        .mensagem("Ordem de serviço já cadastrada.")
                        .build();
            }

            var OrdemServicoEntity = converterEntity(ordemServico);
//            OrdemServicoEntity.setObservacoes(ordemServico.());
            ordemServicoRepository.save(OrdemServicoEntity);

            return RetornoSemObjeto.builder()
                    .statusCode(201)
                    .mensagem("Ordem de serviço criada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao criar Ordem de serviço: " + e.getMessage())
                    .build();
        }
    }

    public RetornoComObjeto<OrdemServicoDto> buscarPorId(Long id) {
        try {
            Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);

            if (ordemServico.isEmpty()) {
                return RetornoComObjeto.<OrdemServicoDto>builder()
                        .statusCode(404)
                        .mensagem("Ordem de serviço não encontrada.")
                        .objeto(null)
                        .build();
            }

            return RetornoComObjeto.<OrdemServicoDto>builder()
                    .statusCode(200)
                    .mensagem("Ordem de serviço encontrada com sucesso.")
                    .objeto(converterParaDto(ordemServico.get()))
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<OrdemServicoDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar Ordem de serviço: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoComObjeto<OrdemServicoDto> atualizarOrdemServico(Long id, OrdemServicoDto ordemServicoAtualizada) {
        try {
            Optional<OrdemServicoEntity> ordemServicoExistente = ordemServicoRepository.findById(id);

            if (ordemServicoExistente.isEmpty()) {
                return RetornoComObjeto.<OrdemServicoDto>builder()
                        .statusCode(404)
                        .mensagem("Ordem de Serviço não encontrada.")
                        .objeto(null)
                        .build();
            }

            OrdemServicoEntity ordemServico = ordemServicoExistente.get();
            atualizarOrdemServicoEntity(ordemServicoAtualizada, ordemServico);

            ordemServicoRepository.save(ordemServico);

            return RetornoComObjeto.<OrdemServicoDto>builder()
                    .statusCode(200)
                    .mensagem("Ordem de Serviço atualizada com sucesso.")
                    .objeto(ordemServicoAtualizada)
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<OrdemServicoDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao atualizar Ordem de Serviço: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoSemObjeto deletarOrdemServico(Long id) {
        try {
            Optional<OrdemServicoEntity> ordemServico = ordemServicoRepository.findById(id);

            if (ordemServico.isEmpty()) {
                return RetornoSemObjeto.builder()
                        .statusCode(404)
                        .mensagem("Ordem de Servico não encontrada.")
                        .build();
            }

            ordemServicoRepository.deleteById(id);

            return RetornoSemObjeto.builder()
                    .statusCode(200)
                    .mensagem("Ordem de Servico deletada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao deletar ordem de Servico: " + e.getMessage())
                    .build();
        }
    }

    private OrdemServicoDto converterParaDto(OrdemServicoEntity entity) {
        return OrdemServicoDto.builder()
                .id(entity.getId())
                .dtConclusao(entity.getDtConclusao())
                .observacoes(entity.getObservacoes())
                .status(entity.getStatus())
                .build();
    }
//
//    private List<OrdemServicoDto> converterListaParaDto(List<OrdemServicoEntity> entities) {
//        return entities.stream()
//                .map(this::converterParaDto)
//                .collect(Collectors.toList());
//    }
//
    public OrdemServicoEntity converterEntity(OrdemServicoDto dto) {
        return OrdemServicoEntity.builder()
                .dtConclusao(dto.getDtConclusao())
                .observacoes(dto.getObservacoes())
                .status(dto.getStatus())
                .build();
    }

    public void atualizarOrdemServicoEntity(OrdemServicoDto dto, OrdemServicoEntity entity) {
        entity.setObservacoes(dto.getObservacoes());
        entity.setStatus(dto.getStatus());
        entity.setDtConclusao(dto.getDtConclusao());
    }

}
