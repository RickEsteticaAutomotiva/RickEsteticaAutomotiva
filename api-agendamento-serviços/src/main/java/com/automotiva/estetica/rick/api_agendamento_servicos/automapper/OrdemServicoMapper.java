package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.OrdemServicoDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.MotivoCancelamentoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.StatusEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.VeiculoEntity;
import org.mapstruct.*;

import java.util.List;
@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    @Mapping(source = "veiculo.id", target = "veiculo")
    @Mapping(source = "status.id", target = "status")
    @Mapping(source = "motivoCancelamento.id", target = "motivo")
    OrdemServicoDto ordemServicoParaOrdemServicoDto(OrdemServicoEntity entity);

    @Mapping(source = "veiculo", target = "veiculo.id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "motivo", target = "motivoCancelamento")
    OrdemServicoEntity ordemServicoDtoParaOrdemServico(OrdemServicoDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void atualizarOrdemServicoEntityFromDto(OrdemServicoDto dto, @MappingTarget OrdemServicoEntity entity);

    List<OrdemServicoDto> ordemServicosParaOrdemServicosDto(List<OrdemServicoEntity> entities);

    // Métodos auxiliares para MapStruct
    default Long map(StatusEntity status) {
        return status == null ? null : status.getId();
    }

    default StatusEntity map(Long id) {
        if (id == null) return null;
        StatusEntity s = new StatusEntity();
        s.setId(id);
        return s;
    }

    default Long map(MotivoCancelamentoEntity motivo) {
        return motivo == null ? null : motivo.getId();
    }

    default MotivoCancelamentoEntity mapMotivo(Long id) {
        if (id == null) return null;
        MotivoCancelamentoEntity m = new MotivoCancelamentoEntity();
        m.setId(id);
        return m;
    }

    default Long map(VeiculoEntity veiculo) {
        return veiculo == null ? null : veiculo.getId();
    }

    default VeiculoEntity mapVeiculo(Long id) {
        if (id == null) return null;
        VeiculoEntity v = new VeiculoEntity();
        v.setId(id);
        return v;
    }
}
