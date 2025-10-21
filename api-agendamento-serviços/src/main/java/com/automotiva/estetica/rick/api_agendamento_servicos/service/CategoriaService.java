package com.automotiva.estetica.rick.api_agendamento_servicos.service;

import com.automotiva.estetica.rick.api_agendamento_servicos.automapper.CategoriaMapper;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.CategoriaDto;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.CategoriaEntity;
import com.automotiva.estetica.rick.api_agendamento_servicos.exception.RecursoNaoEncontradaException;
import com.automotiva.estetica.rick.api_agendamento_servicos.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public List<CategoriaDto> buscarCategorias() {
        List<CategoriaEntity> categorias = categoriaRepository.findAll();
        return categorias.stream()
                .map(categoriaMapper::categoriaEntiryParaCategoriaDto)
                .collect(Collectors.toList());
    }

    public void criarCategoria(CategoriaDto categoriaDto) {
        CategoriaEntity novaCategoria = categoriaMapper.categoriaDtoParaCategoriaEntity(categoriaDto);
        categoriaRepository.save(novaCategoria);
    }

    public CategoriaDto atualizarCategoria(Long id, CategoriaDto categoriaDto) {
        if (!categoriaRepository.existsById(id)) {
            throw RecursoNaoEncontradaException
                    .builder()
                    .mensagem("Categoria não encontrada com o ID: " + id)
                    .detalhes("")
                    .build();
        }

        CategoriaEntity categoriaEntity = categoriaMapper.categoriaDtoParaCategoriaEntity(categoriaDto);
        categoriaEntity.setId(id);

        CategoriaEntity categoriaSalva = categoriaRepository.save(categoriaEntity);
        return categoriaMapper.categoriaEntiryParaCategoriaDto(categoriaSalva);
    }
}
