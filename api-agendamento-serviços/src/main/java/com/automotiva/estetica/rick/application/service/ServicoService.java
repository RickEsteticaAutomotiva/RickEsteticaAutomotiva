package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.PageableFactory;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.application.port.in.ServicoUseCase;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicoService implements ServicoUseCase {

    private final ServicoRepositoryPort servicoRepositoryPort;

    @Override
    public Page<ServicoResponse> buscarTodos(PageRequest pageRequest) {
        Pageable pageable = PageableFactory.from(pageRequest);
        return servicoRepositoryPort.buscarTodos(pageRequest.getFiltro(), pageable).map(this::toResponse);
    }

    @Override
    public ServicoResponse buscarPorId(Long id) {
        return servicoRepositoryPort.buscarPorId(id).map(this::toResponse)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("o serviço com id " + id + " não foi encontrado").detalhes("").build());
    }

    @Override
    public ServicoResponse criar(ServicoRequest request) {
        Servico servico = Servico.builder().nome(request.getNome()).descricao(request.getDescricao())
                .preco(request.getPreco()).imagem(request.getImagem()).duracaoHoras(request.getDuracaoHoras())
                .categoria(Categoria.builder().id(request.getCategoriaId()).build()).build();
        return toResponse(servicoRepositoryPort.salvar(servico));
    }

    @Override
    public ServicoResponse atualizar(Long id, ServicoRequest request) {
        Servico servico = servicoRepositoryPort.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException
                .builder().mensagem("o serviço com id " + id + " não foi encontrado").detalhes("").build());

        servico.atualizar(request.getNome(), request.getDescricao(), request.getPreco(), request.getImagem(),
                request.getCategoriaId(), request.getDuracaoHoras());

        return toResponse(servicoRepositoryPort.salvar(servico));
    }

    @Override
    public void deletar(Long id) {
        if (!servicoRepositoryPort.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder().mensagem("o serviço com id " + id + " não foi encontrado")
                    .detalhes("").build();
        }
        servicoRepositoryPort.deletarPorId(id);
    }

    private ServicoResponse toResponse(Servico s) {
        return ServicoResponse.builder().id(s.getId()).nome(s.getNome()).descricao(s.getDescricao()).preco(s.getPreco())
                .imagem(s.getImagem()).duracaoHoras(s.getDuracaoHoras())
                .categoriaId(s.getCategoria() != null ? s.getCategoria().getId() : null)
                .categoriaNome(s.getCategoria() != null ? s.getCategoria().getNome() : null).build();
    }
}
