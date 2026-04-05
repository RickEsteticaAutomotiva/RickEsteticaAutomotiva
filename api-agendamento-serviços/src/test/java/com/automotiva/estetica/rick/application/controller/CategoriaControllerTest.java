package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.response.CategoriaResponse;
import com.automotiva.estetica.rick.application.mapper.CategoriaDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.usecase.AtualizarCategoriaUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarCategoriaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarCategoriasUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CategoriaController")
class CategoriaControllerTest {

    @Mock
    private CadastrarCategoriaUseCase cadastrarCategoriaUseCase;

    @Mock
    private ListarCategoriasUseCase listarCategoriasUseCase;

    @Mock
    private AtualizarCategoriaUseCase atualizarCategoriaUseCase;

    @Mock
    private CategoriaDTOMapper categoriaDTOMapper;

    @InjectMocks
    private CategoriaController categoriaController;

    @Test
    @DisplayName("buscarTodas deve mapear categorias e retornar 200")
    void buscarTodas_deveMapearERetornar200() {
        Categoria categoria = Categoria.builder().id(1L).nome("Polimento").build();
        CategoriaResponse response = CategoriaResponse.builder().id(1L).nome("Polimento").build();
        when(listarCategoriasUseCase.execute()).thenReturn(List.of(categoria));
        when(categoriaDTOMapper.toResponse(categoria)).thenReturn(response);

        var httpResponse = categoriaController.buscarTodas();

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getBody());
        assertEquals(1, httpResponse.getBody().size());
        assertEquals("Polimento", httpResponse.getBody().getFirst().getNome());
        verify(listarCategoriasUseCase).execute();
    }

    @Test
    @DisplayName("criar deve delegar cadastro e retornar 201")
    void criar_deveDelegarERetornar201() {
        CategoriaRequest request = CategoriaRequest.builder().nome("Higienizacao").build();
        Categoria categoria = Categoria.builder().nome("Higienizacao").build();
        when(categoriaDTOMapper.toDomain(request)).thenReturn(categoria);

        var httpResponse = categoriaController.criar(request);

        assertEquals(HttpStatus.CREATED, httpResponse.getStatusCode());
        verify(categoriaDTOMapper).toDomain(request);
        verify(cadastrarCategoriaUseCase).execute(categoria);
    }

    @Test
    @DisplayName("atualizar deve delegar use case e retornar categoria")
    void atualizar_deveDelegarERetornar200() {
        CategoriaRequest request = CategoriaRequest.builder().nome("Lavagem").build();
        Categoria categoriaAtualizada = Categoria.builder().id(9L).nome("Lavagem").build();
        CategoriaResponse response = CategoriaResponse.builder().id(9L).nome("Lavagem").build();
        when(atualizarCategoriaUseCase.execute(9L, "Lavagem")).thenReturn(categoriaAtualizada);
        when(categoriaDTOMapper.toResponse(categoriaAtualizada)).thenReturn(response);

        var httpResponse = categoriaController.atualizar(9L, request);

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
        verify(atualizarCategoriaUseCase).execute(9L, "Lavagem");
    }
}

