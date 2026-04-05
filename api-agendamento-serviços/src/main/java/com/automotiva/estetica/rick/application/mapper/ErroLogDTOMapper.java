package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ErroLogDTOMapper {

    ErroLogResponse toResponse(ErroLog erroLog);
}
