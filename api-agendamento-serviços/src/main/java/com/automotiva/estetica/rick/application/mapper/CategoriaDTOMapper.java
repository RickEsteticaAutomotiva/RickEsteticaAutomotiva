package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.response.CategoriaResponse;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct para conversão entre DTOs de Application e entidades de
 * Domínio.
 *
 * <p>
 * Responsável por: - Converter {@link CategoriaRequest} → {@link Categoria} -
 * Converter {@link Categoria} → {@link CategoriaResponse}
 *
 * <p>
 * Camada: application/mapper.
 */
@Mapper(componentModel = "spring")
public interface CategoriaDTOMapper {

    /**
     * Converte DTO de requisição para entidade de domínio.
     */
    Categoria toDomain(CategoriaRequest request);

    /**
     * Converte entidade de domínio para DTO de resposta.
     */
    CategoriaResponse toResponse(Categoria categoria);
}
