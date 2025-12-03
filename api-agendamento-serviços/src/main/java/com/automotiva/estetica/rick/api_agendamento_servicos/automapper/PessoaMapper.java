package com.automotiva.estetica.rick.api_agendamento_servicos.automapper;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaAtualizadaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaCadastroDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.PessoaTokenDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PessoaMapper {

    PessoaMapper INSTANCE = Mappers.getMapper(PessoaMapper.class);

    PessoaDto pessoaParaPessoaDto(PessoaEntity pessoa);

    PessoaEntity pessoaDtoParaPessoa(PessoaDto pessoa);

    @Mapping(target = "id", ignore = true)
    PessoaEntity pessoaCadastroDtoParaPessoaEntity(PessoaCadastroDto dto);

    PessoaCadastroDto pessoaEntityParaPessoaCadastroDto(PessoaEntity entity);

    void atualizarPessoaEntityFromDto(PessoaAtualizadaDto dto, @MappingTarget PessoaEntity entity);

    List<PessoaDto> pessoasParaPessoasDto(List<PessoaEntity> pessoas);

    List<PessoaEntity> pessoasDtoParaPessoas(List<PessoaDto> pessoasDto);

    PessoaTokenDto PessoaDtoParaPessoaToken(PessoaDto pessoa, String token);
    PessoaTokenDto PessoaParaPessoaToken(PessoaEntity pessoa, String token);
}
