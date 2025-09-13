package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.ServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComListaObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoComObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.infra.RetornoSemObjeto;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {
    @Autowired
    ServicoRepository servicoRepository;

    public RetornoComListaObjeto<ServicoEntity> buscarTodos() {

        try {
            List<ServicoEntity> Servicos = servicoRepository.findAll();

            if (Servicos.isEmpty()) {
                return RetornoComListaObjeto.<ServicoEntity>builder()
                        .statusCode(404)
                        .mensagem("Nenhuma  de serviço encontrada.")
                        .objeto(List.of())
                        .build();
            }

            return RetornoComListaObjeto.<ServicoEntity>builder()
                    .statusCode(200)
                    .mensagem(" de serviço encontradas com sucesso.")
                    .objeto(Servicos)
                    .build();

        } catch (Exception e) {
            return RetornoComListaObjeto.<ServicoEntity>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar Servicos: " + e.getMessage())
                    .objeto(List.of())
                    .build();
        }
    }

    public RetornoSemObjeto criarServico(ServicoDto Servico) {
        try {

            if (servicoRepository.existsById(Servico.getId())) {
                return RetornoSemObjeto.builder()
                        .statusCode(400)
                        .mensagem(" de serviço já cadastrada.")
                        .build();
            }

            var ServicoEntity = converterEntity(Servico);
//            ServicoEntity.setObservacoes(Servico.());
            servicoRepository.save(ServicoEntity);

            return RetornoSemObjeto.builder()
                    .statusCode(201)
                    .mensagem(" de serviço criada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao criar  de serviço: " + e.getMessage())
                    .build();
        }
    }

    public RetornoComObjeto<ServicoDto> buscarPorId(Long id) {
        try {
            Optional<ServicoEntity> Servico = servicoRepository.findById(id);

            if (Servico.isEmpty()) {
                return RetornoComObjeto.<ServicoDto>builder()
                        .statusCode(404)
                        .mensagem(" de serviço não encontrada.")
                        .objeto(null)
                        .build();
            }

            return RetornoComObjeto.<ServicoDto>builder()
                    .statusCode(200)
                    .mensagem(" de serviço encontrada com sucesso.")
                    .objeto(converterParaDto(Servico.get()))
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<ServicoDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao buscar  de serviço: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoComObjeto<ServicoDto> atualizarServico(Long id, ServicoDto ServicoAtualizada) {
        try {
            Optional<ServicoEntity> ServicoExistente = servicoRepository.findById(id);

            if (ServicoExistente.isEmpty()) {
                return RetornoComObjeto.<ServicoDto>builder()
                        .statusCode(404)
                        .mensagem(" de Serviço não encontrada.")
                        .objeto(null)
                        .build();
            }

            ServicoEntity Servico = ServicoExistente.get();
            atualizarServicoEntity(ServicoAtualizada, Servico);

            servicoRepository.save(Servico);

            return RetornoComObjeto.<ServicoDto>builder()
                    .statusCode(200)
                    .mensagem(" de Serviço atualizada com sucesso.")
                    .objeto(ServicoAtualizada)
                    .build();

        } catch (Exception e) {
            return RetornoComObjeto.<ServicoDto>builder()
                    .statusCode(500)
                    .mensagem("Erro ao atualizar  de Serviço: " + e.getMessage())
                    .objeto(null)
                    .build();
        }
    }

    public RetornoSemObjeto deletarServico(Long id) {
        try {
            Optional<ServicoEntity> Servico = servicoRepository.findById(id);

            if (Servico.isEmpty()) {
                return RetornoSemObjeto.builder()
                        .statusCode(404)
                        .mensagem(" de Servico não encontrada.")
                        .build();
            }

            servicoRepository.deleteById(id);

            return RetornoSemObjeto.builder()
                    .statusCode(200)
                    .mensagem(" de Servico deletada com sucesso.")
                    .build();

        } catch (Exception e) {
            return RetornoSemObjeto.builder()
                    .statusCode(500)
                    .mensagem("Erro ao deletar  de Servico: " + e.getMessage())
                    .build();
        }
    }

    private ServicoDto converterParaDto(ServicoEntity entity) {
        return ServicoDto.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .build();
    }
//
//    private List<ServicoDto> converterListaParaDto(List<ServicoEntity> entities) {
//        return entities.stream()
//                .map(this::converterParaDto)
//                .collect(Collectors.toList());
//    }
//
    public ServicoEntity converterEntity(ServicoDto dto) {
        return ServicoEntity.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .build();
    }

    public void atualizarServicoEntity(ServicoDto dto, ServicoEntity entity) {
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setPreco(dto.getPreco());
    }

}
