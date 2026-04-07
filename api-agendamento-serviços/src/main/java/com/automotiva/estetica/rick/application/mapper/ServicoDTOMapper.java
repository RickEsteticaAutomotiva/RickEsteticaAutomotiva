package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.domain.entity.Servico;
import java.time.LocalTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ServicoDTOMapper {

    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "duracaoMinutos", source = "duracaoHoras", qualifiedByName = "horasParaMinutos")
    Servico toDomain(ServicoRequest request);

    @Mapping(target = "duracaoHoras", source = "duracaoMinutos", qualifiedByName = "minutosParaHoras")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNome", source = "categoria.nome")
    ServicoResponse toResponse(Servico servico);

    @Named("horasParaMinutos")
    default Integer horasParaMinutos(LocalTime duracaoHoras) {
        if (duracaoHoras == null)
            return null;
        return (duracaoHoras.getHour() * 60) + duracaoHoras.getMinute();
    }

    @Named("minutosParaHoras")
    default LocalTime minutosParaHoras(Integer duracaoMinutos) {
        if (duracaoMinutos == null)
            return null;
        return LocalTime.MIDNIGHT.plusMinutes(duracaoMinutos);
    }
}
