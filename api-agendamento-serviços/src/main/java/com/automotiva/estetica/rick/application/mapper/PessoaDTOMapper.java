package com.automotiva.estetica.rick.application.mapper;

import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct para conversão entre DTOs de Application e entidades de
 * Domínio.
 *
 * <p>
 * Responsável por: - Converter {@link PessoaCadastroRequest} → {@link Pessoa} -
 * Converter {@link Pessoa} → {@link PessoaResponse}
 *
 * <p>
 * Camada: application/mapper.
 */
@Mapper(componentModel = "spring")
public interface PessoaDTOMapper {

    /**
     * Converte DTO de cadastro para entidade de domínio.
     */
    Pessoa toDomain(PessoaCadastroRequest request);

    /**
     * Converte entidade de domínio para DTO de resposta.
     */
    PessoaResponse toResponse(Pessoa pessoa);
}
