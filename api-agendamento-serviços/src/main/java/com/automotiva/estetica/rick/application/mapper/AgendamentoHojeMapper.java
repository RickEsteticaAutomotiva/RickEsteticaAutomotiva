package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeResponse;
import com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeServicoResponse;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AgendamentoHojeMapper {

    default AgendamentoHojeResponse toResponseWithCalculations(OrdemServico ordemServico, List<ItemServico> itens) {

        List<AgendamentoHojeServicoResponse> servicos = itens.stream().map(this::servicoToResponse).toList();

        BigDecimal precoTotal = itens.stream().map(ItemServico::getPreco).reduce(BigDecimal.ZERO, BigDecimal::add);

        AgendamentoHojeResponse response = AgendamentoHojeResponse.builder().id(ordemServico.getId())
                .dataHora(ordemServico.getDataAgendamento())
                .status(ordemServico.getStatus() != null ? ordemServico.getStatus().getDescricao() : null)
                .precoMinimo(ordemServico.getPrecoMinimo()).precoTotal(precoTotal)
                .observacoes(ordemServico.getObservacoes()).servicos(servicos).build();

        // Montar cliente
        if (ordemServico.getVeiculo() != null && ordemServico.getVeiculo().getPessoa() != null) {
            response.setCliente(com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeClienteResponse
                    .builder().id(ordemServico.getVeiculo().getPessoa().getId())
                    .nome(ordemServico.getVeiculo().getPessoa().getNome())
                    .email(ordemServico.getVeiculo().getPessoa().getEmail())
                    .telefone(ordemServico.getVeiculo().getPessoa().getTelefone()).build());
        }

        // Montar veículo
        if (ordemServico.getVeiculo() != null) {
            response.setVeiculo(com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeVeiculoResponse
                    .builder().id(ordemServico.getVeiculo().getId()).placa(ordemServico.getVeiculo().getPlaca())
                    .modelo(ordemServico.getVeiculo().getModelo()).marca(ordemServico.getVeiculo().getMarca())
                    .ano(ordemServico.getVeiculo().getAno()).cor(ordemServico.getVeiculo().getCor()).build());
        }

        return response;
    }

    default AgendamentoHojeServicoResponse servicoToResponse(ItemServico itemServico) {
        return AgendamentoHojeServicoResponse.builder().id(itemServico.getServico().getId())
                .nome(itemServico.getServico().getNome()).descricao(itemServico.getServico().getDescricao())
                .preco(itemServico.getPreco()).build();
    }
}
