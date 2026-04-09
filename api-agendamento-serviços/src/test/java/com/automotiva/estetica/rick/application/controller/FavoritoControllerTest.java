package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.FavoritoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoFavoritoResponse;
import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.usecase.AdicionarFavoritoUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarFavoritoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.RemoverFavoritoUseCase;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de FavoritoController")
class FavoritoControllerTest {

    @Mock
    private AdicionarFavoritoUseCase adicionarFavoritoUseCase;

    @Mock
    private RemoverFavoritoUseCase removerFavoritoUseCase;

    @Mock
    private ListarFavoritoPessoaUseCase listarFavoritoPessoaUseCase;

    @InjectMocks
    private FavoritoController favoritoController;

    @Test
    @DisplayName("listar deve mapear resposta e retornar 200")
    void listar_deveMapearRespostaERetornar200() {
        Long idPessoa = 20L;
        Servico servico = Servico.builder().id(8L).nome("Cristalizacao").descricao("Cristalizacao de pintura")
                .preco(new BigDecimal("220.00")).imagem("foto.png").build();
        when(listarFavoritoPessoaUseCase.execute(idPessoa))
                .thenReturn(List.of(Favorito.builder().id(33L).servico(servico).build()));

        var response = favoritoController.listar(idPessoa);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ServicoFavoritoResponse> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals(33L, body.getFirst().getIdFavorito());
        assertEquals(8L, body.getFirst().getIdServico());
        assertEquals("Cristalizacao", body.getFirst().getNome());
    }

    @Test
    @DisplayName("adicionar deve delegar para use case e retornar 201")
    void adicionar_deveDelegarERetornar201() {
        FavoritoRequest request = FavoritoRequest.builder().idPessoa(9L).idServico(4L).build();

        var response = favoritoController.adicionar(request);

        verify(adicionarFavoritoUseCase).execute(9L, 4L);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("remover deve delegar para use case e retornar 204")
    void remover_deveDelegarERetornar204() {
        var response = favoritoController.remover(12L);

        verify(removerFavoritoUseCase).execute(12L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
