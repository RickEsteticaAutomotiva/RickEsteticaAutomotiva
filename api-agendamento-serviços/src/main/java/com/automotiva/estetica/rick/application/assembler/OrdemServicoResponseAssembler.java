package com.automotiva.estetica.rick.application.assembler;

import com.automotiva.estetica.rick.application.dto.response.OrdemServicoClienteResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeClienteResponse;
import com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeResponse;
import com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.AgendamentoHojeVeiculoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDetalheResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoServicoResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoVeiculoResumoResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.StatusResumoResponse;
import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoResponseAssembler {

    public OrdemServicoResponse toResponse(OrdemServico ordemServico, List<ItemServico> itens) {
        return OrdemServicoResponse.builder().id(ordemServico.getId())
                .dataAgendamento(ordemServico.getDataAgendamento()).precoMinimo(ordemServico.getPrecoMinimo())
                .veiculo(toVeiculoResumo(ordemServico)).status(toStatusResumo(ordemServico))
                .observacoes(ordemServico.getObservacoes()).dtConclusao(ordemServico.getDtConclusao())
                .motivo(ordemServico.getMotivoCancelamento() != null
                        ? ordemServico.getMotivoCancelamento().getId()
                        : null)
                .cliente(toClienteResumo(ordemServico)).servicos(toServicosResumo(itens)).build();
    }

    public OrdemServicoResumoResponse toResumoGestao(OrdemServico ordemServico, List<ItemServico> itens) {
        return OrdemServicoResumoResponse.builder().id(ordemServico.getId())
                .dataAgendamento(ordemServico.getDataAgendamento()).dataConclusao(ordemServico.getDtConclusao())
                .status(toStatusResumo(ordemServico)).observacoes(ordemServico.getObservacoes())
                .valorTotal(calcularValorTotal(itens)).cliente(toClienteResumo(ordemServico))
                .veiculo(toVeiculoResumo(ordemServico)).servicos(toServicosResumo(itens)).build();
    }

    public OrdemServicoDetalheResponse toDetalheGestao(OrdemServico ordemServico, List<ItemServico> itens) {
        return OrdemServicoDetalheResponse.builder().id(ordemServico.getId())
                .dataAgendamento(ordemServico.getDataAgendamento()).dataConclusao(ordemServico.getDtConclusao())
                .status(toStatusResumo(ordemServico)).observacoes(ordemServico.getObservacoes())
                .valorTotal(calcularValorTotal(itens)).cliente(toClienteResumo(ordemServico))
                .veiculo(toVeiculoResumo(ordemServico)).servicos(toServicosResumo(itens)).build();
    }

    public AgendamentoHojeResponse toAgendamentoHojeResponse(OrdemServico ordemServico, List<ItemServico> itens) {
        return AgendamentoHojeResponse.builder().id(ordemServico.getId())
                .dataHora(ordemServico.getDataAgendamento())
                .status(ordemServico.getStatus() != null ? ordemServico.getStatus().getDescricao() : null)
                .precoMinimo(ordemServico.getPrecoMinimo()).precoTotal(calcularValorTotal(itens))
                .observacoes(ordemServico.getObservacoes()).cliente(toAgendamentoHojeCliente(ordemServico))
                .veiculo(toAgendamentoHojeVeiculo(ordemServico)).servicos(toAgendamentoHojeServicos(itens)).build();
    }

    private BigDecimal calcularValorTotal(List<ItemServico> itens) {
        return itens.stream().map(ItemServico::getPreco).filter(Objects::nonNull).reduce(BigDecimal.ZERO,
                BigDecimal::add);
    }

    private OrdemServicoClienteResumoResponse toClienteResumo(OrdemServico ordemServico) {
        if (ordemServico.getVeiculo() == null || ordemServico.getVeiculo().getPessoa() == null) {
            return null;
        }
        return OrdemServicoClienteResumoResponse.builder().id(ordemServico.getVeiculo().getPessoa().getId())
                .nome(ordemServico.getVeiculo().getPessoa().getNome()).build();
    }

    private OrdemServicoVeiculoResumoResponse toVeiculoResumo(OrdemServico ordemServico) {
        if (ordemServico.getVeiculo() == null) {
            return null;
        }
        return OrdemServicoVeiculoResumoResponse.builder().id(ordemServico.getVeiculo().getId())
                .marca(ordemServico.getVeiculo().getMarca()).modelo(ordemServico.getVeiculo().getModelo())
                .placa(ordemServico.getVeiculo().getPlaca()).build();
    }

    private StatusResumoResponse toStatusResumo(OrdemServico ordemServico) {
        if (ordemServico.getStatus() == null) {
            return null;
        }
        return StatusResumoResponse.builder().id(ordemServico.getStatus().getId())
                .descricao(ordemServico.getStatus().getDescricao()).build();
    }

    private List<OrdemServicoServicoResumoResponse> toServicosResumo(List<ItemServico> itens) {
        return itens.stream().map(item -> OrdemServicoServicoResumoResponse.builder()
                .id(item.getServico() != null ? item.getServico().getId() : null)
                .nome(item.getServico() != null ? item.getServico().getNome() : null).valorAplicado(item.getPreco())
                .preco(item.getServico() != null ? item.getServico().getPreco() : null).build()).toList();
    }

    private AgendamentoHojeClienteResponse toAgendamentoHojeCliente(OrdemServico ordemServico) {
        if (ordemServico.getVeiculo() == null || ordemServico.getVeiculo().getPessoa() == null) {
            return null;
        }

        return AgendamentoHojeClienteResponse.builder().id(ordemServico.getVeiculo().getPessoa().getId())
                .nome(ordemServico.getVeiculo().getPessoa().getNome())
                .email(ordemServico.getVeiculo().getPessoa().getEmail())
                .telefone(ordemServico.getVeiculo().getPessoa().getTelefone()).build();
    }

    private AgendamentoHojeVeiculoResponse toAgendamentoHojeVeiculo(OrdemServico ordemServico) {
        if (ordemServico.getVeiculo() == null) {
            return null;
        }

        return AgendamentoHojeVeiculoResponse.builder().id(ordemServico.getVeiculo().getId())
                .placa(ordemServico.getVeiculo().getPlaca()).modelo(ordemServico.getVeiculo().getModelo())
                .marca(ordemServico.getVeiculo().getMarca()).ano(ordemServico.getVeiculo().getAno())
                .cor(ordemServico.getVeiculo().getCor()).build();
    }

    private List<AgendamentoHojeServicoResponse> toAgendamentoHojeServicos(List<ItemServico> itens) {
        return itens.stream().map(item -> AgendamentoHojeServicoResponse.builder()
                .id(item.getServico() != null ? item.getServico().getId() : null)
                .nome(item.getServico() != null ? item.getServico().getNome() : null)
                .descricao(item.getServico() != null ? item.getServico().getDescricao() : null)
                .preco(item.getPreco()).build()).toList();
    }
}
