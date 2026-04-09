package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.CarrinhoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoCarrinhoResponse;
import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.usecase.AdicionarCarrinhoUseCase;
import com.automotiva.estetica.rick.domain.usecase.LimparCarrinhoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.ListarCarrinhoPessoaUseCase;
import com.automotiva.estetica.rick.domain.usecase.RemoverCarrinhoUseCase;
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
@DisplayName("Testes de CarrinhoController")
class CarrinhoControllerTest {

    @Mock
    private AdicionarCarrinhoUseCase adicionarCarrinhoUseCase;

    @Mock
    private RemoverCarrinhoUseCase removerCarrinhoUseCase;

    @Mock
    private LimparCarrinhoPessoaUseCase limparCarrinhoPessoaUseCase;

    @Mock
    private ListarCarrinhoPessoaUseCase listarCarrinhoPessoaUseCase;

    @InjectMocks
    private CarrinhoController carrinhoController;

    @Test
    @DisplayName("listar deve mapear resposta e retornar 200")
    void listar_deveMapearRespostaERetornar200() {
        Long idPessoa = 10L;
        Servico servico = Servico.builder().id(3L).nome("Polimento").descricao("Polimento tecnico")
                .preco(new BigDecimal("150.00")).imagem("img.png").build();
        when(listarCarrinhoPessoaUseCase.execute(idPessoa))
                .thenReturn(List.of(Carrinho.builder().id(99L).servico(servico).build()));

        var response = carrinhoController.listar(idPessoa);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ServicoCarrinhoResponse> body = response.getBody();
        assertEquals(1, body.size());
        assertEquals(99L, body.getFirst().getIdCarrinho());
        assertEquals(3L, body.getFirst().getIdServico());
        assertEquals("Polimento", body.getFirst().getNome());
    }

    @Test
    @DisplayName("adicionar deve delegar para use case e retornar 201")
    void adicionar_deveDelegarERetornar201() {
        CarrinhoRequest request = CarrinhoRequest.builder().idPessoa(1L).idServico(2L).build();

        var response = carrinhoController.adicionar(request);

        verify(adicionarCarrinhoUseCase).execute(1L, 2L);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("remover deve delegar para use case e retornar 204")
    void remover_deveDelegarERetornar204() {
        var response = carrinhoController.remover(7L);

        verify(removerCarrinhoUseCase).execute(7L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("limpar deve delegar para use case e retornar 204")
    void limpar_deveDelegarERetornar204() {
        var response = carrinhoController.limpar(5L);

        verify(limparCarrinhoPessoaUseCase).execute(5L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
