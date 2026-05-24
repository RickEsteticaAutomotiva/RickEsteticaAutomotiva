package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.application.mapper.ServicoDTOMapper;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.usecase.AtualizarServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarServicoPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.CadastrarServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.DeletarServicoUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarServicosUseCase;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ServicoController")
class ServicoControllerTest {

    @Mock
    private CadastrarServicoUseCase cadastrarServicoUseCase;

    @Mock
    private BuscarServicoPorIdUseCase buscarServicoPorIdUseCase;

    @Mock
    private ListarServicosUseCase listarServicosUseCase;

    @Mock
    private AtualizarServicoUseCase atualizarServicoUseCase;

    @Mock
    private DeletarServicoUseCase deletarServicoUseCase;

    @Mock
    private ServicoDTOMapper servicoDTOMapper;

    @InjectMocks
    private ServicoController servicoController;

    @Test
    @DisplayName("buscarTodos deve retornar pagina de servicos")
    void buscarTodos_deveRetornarPagina() {
        PageRequest request = PageRequest.builder().pagina(0).tamanho(10).filtro("lav").ordenarPor("id").build();
        Servico servico = Servico.builder().id(1L).nome("Lavagem").build();
        ServicoResponse servicoResponse = ServicoResponse.builder().id(1L).nome("Lavagem").build();
        Page<Servico> pagina = new PageImpl<>(List.of(servico), org.springframework.data.domain.PageRequest.of(0, 10),
                1);
        when(listarServicosUseCase.execute("lav",
                org.springframework.data.domain.PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("id"))))
                .thenReturn(pagina);
        when(servicoDTOMapper.toResponse(servico)).thenReturn(servicoResponse);

        var httpResponse = servicoController.buscarTodos(request);

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getBody());
        assertEquals(1, httpResponse.getBody().getContent().size());
        assertEquals("Lavagem", httpResponse.getBody().getContent().getFirst().getNome());
    }

    @Test
    @DisplayName("buscarPorId deve delegar e retornar 200")
    void buscarPorId_deveDelegarERetornar200() {
        Servico servico = Servico.builder().id(2L).nome("Polimento").build();
        ServicoResponse response = ServicoResponse.builder().id(2L).nome("Polimento").build();
        when(buscarServicoPorIdUseCase.execute(2L)).thenReturn(servico);
        when(servicoDTOMapper.toResponse(servico)).thenReturn(response);

        var httpResponse = servicoController.buscarPorId(2L);

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
        verify(buscarServicoPorIdUseCase).execute(2L);
    }

    @Test
    @DisplayName("criar deve delegar cadastro e retornar 201")
    void criar_deveDelegarERetornar201() {
        ServicoRequest request = ServicoRequest.builder().nome("Vitrificacao").preco(new BigDecimal("299.90"))
                .duracaoHoras(LocalTime.of(2, 0)).categoriaId(3L).build();
        Servico servicoDomain = Servico.builder().nome("Vitrificacao").build();
        Servico servicoCriado = Servico.builder().id(3L).nome("Vitrificacao").build();
        ServicoResponse response = ServicoResponse.builder().id(3L).nome("Vitrificacao").build();
        when(servicoDTOMapper.toDomain(request)).thenReturn(servicoDomain);
        when(cadastrarServicoUseCase.execute(servicoDomain)).thenReturn(servicoCriado);
        when(servicoDTOMapper.toResponse(servicoCriado)).thenReturn(response);

        var httpResponse = servicoController.criar(request);

        assertEquals(HttpStatus.CREATED, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
    }

    @Test
    @DisplayName("atualizar deve delegar use case e retornar 200")
    void atualizar_deveDelegarERetornar200() {
        ServicoRequest request = ServicoRequest.builder().nome("Higienizacao").descricao("Interna")
                .preco(new BigDecimal("180.00")).imagem("img.png").categoriaId(4L).duracaoHoras(LocalTime.of(1, 30))
                .build();
        Integer duracaoMinutos = 90;
        Servico servicoAtualizado = Servico.builder().id(4L).nome("Higienizacao").build();
        ServicoResponse response = ServicoResponse.builder().id(4L).nome("Higienizacao").build();
        when(servicoDTOMapper.horasParaMinutos(request.getDuracaoHoras())).thenReturn(duracaoMinutos);
        when(atualizarServicoUseCase.execute(4L, request.getNome(), request.getDescricao(), request.getPreco(),
                request.getImagem(), request.getCategoriaId(), duracaoMinutos)).thenReturn(servicoAtualizado);
        when(servicoDTOMapper.toResponse(servicoAtualizado)).thenReturn(response);

        var httpResponse = servicoController.atualizar(4L, request);

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
        verify(servicoDTOMapper).horasParaMinutos(request.getDuracaoHoras());
        verify(atualizarServicoUseCase).execute(4L, request.getNome(), request.getDescricao(), request.getPreco(),
                request.getImagem(), request.getCategoriaId(), duracaoMinutos);
    }

    @Test
    @DisplayName("deletar deve delegar e retornar 204")
    void deletar_deveDelegarERetornar204() {
        var httpResponse = servicoController.deletar(8L);

        verify(deletarServicoUseCase).execute(8L);
        assertEquals(HttpStatus.NO_CONTENT, httpResponse.getStatusCode());
    }
}
