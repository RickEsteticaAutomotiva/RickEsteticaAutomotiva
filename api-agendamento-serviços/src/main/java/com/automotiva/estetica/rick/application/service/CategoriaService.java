package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.response.CategoriaResponse;
import com.automotiva.estetica.rick.application.port.in.CategoriaUseCase;
import com.automotiva.estetica.rick.application.port.out.CategoriaRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {

    private final CategoriaRepositoryPort categoriaRepositoryPort;

    @Override
    public List<CategoriaResponse> buscarTodas() {
        return categoriaRepositoryPort.buscarTodas().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void criar(CategoriaRequest request) {
        categoriaRepositoryPort.salvar(
                Categoria.builder().nome(request.getNome()).build());
    }

    @Override
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepositoryPort
                .buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Categoria não encontrada com o ID: " + id)
                        .detalhes("")
                        .build());
        categoria.atualizar(request.getNome());
        return toResponse(categoriaRepositoryPort.salvar(categoria));
    }

    private CategoriaResponse toResponse(Categoria c) {
        return CategoriaResponse.builder().id(c.getId()).nome(c.getNome()).build();
    }
}
