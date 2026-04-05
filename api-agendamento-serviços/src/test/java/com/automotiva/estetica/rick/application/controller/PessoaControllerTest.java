package com.automotiva.estetica.rick.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.application.dto.request.LoginRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaAtualizacaoRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.SenhaRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import com.automotiva.estetica.rick.application.security.OwnershipValidator;
import com.automotiva.estetica.rick.application.service.PessoaApplicationService;
import java.time.LocalDate;
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
@DisplayName("Testes de PessoaController")
class PessoaControllerTest {

    @Mock
    private PessoaApplicationService pessoaUseCase;

    @Mock
    private OwnershipValidator ownershipValidator;

    @InjectMocks
    private PessoaController pessoaController;

    @Test
    @DisplayName("buscarTodos deve delegar e retornar 200")
    void buscarTodos_deveDelegarERetornar200() {
        PageRequest request = PageRequest.builder().pagina(0).tamanho(10).filtro("ana").build();
        Page<PessoaResponse> pagina = new PageImpl<>(List.of(PessoaResponse.builder().id(1L).nome("Ana").build()));
        when(pessoaUseCase.buscarTodos(request)).thenReturn(pagina);

        var httpResponse = pessoaController.buscarTodos(request);

        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getBody());
        assertEquals(1, httpResponse.getBody().getContent().size());
        verify(pessoaUseCase).buscarTodos(request);
    }

    @Test
    @DisplayName("buscarPorId deve validar ownership e retornar 200")
    void buscarPorId_deveValidarOwnershipERetornar200() {
        PessoaResponse response = PessoaResponse.builder().id(7L).nome("Carlos").build();
        when(pessoaUseCase.buscarPorId(7L)).thenReturn(response);

        var httpResponse = pessoaController.buscarPorId(7L);

        verify(ownershipValidator).validarPropriedade(7L);
        verify(pessoaUseCase).buscarPorId(7L);
        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
    }

    @Test
    @DisplayName("cadastrar deve delegar e retornar 201")
    void cadastrar_deveDelegarERetornar201() {
        PessoaCadastroRequest request = PessoaCadastroRequest.builder().nome("Bea").cpf("123").email("bea@x.com")
                .senha("123456").build();
        PessoaResponse response = PessoaResponse.builder().id(10L).nome("Bea").build();
        when(pessoaUseCase.cadastrar(request)).thenReturn(response);

        var httpResponse = pessoaController.cadastrar(request);

        verify(pessoaUseCase).cadastrar(request);
        assertEquals(HttpStatus.CREATED, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
    }

    @Test
    @DisplayName("atualizar deve validar ownership e retornar 200")
    void atualizar_deveValidarOwnershipERetornar200() {
        PessoaAtualizacaoRequest request = PessoaAtualizacaoRequest.builder().nome("Novo Nome")
                .dataNascimento(LocalDate.of(1990, 1, 1)).build();
        PessoaResponse response = PessoaResponse.builder().id(3L).nome("Novo Nome").build();
        when(pessoaUseCase.atualizar(3L, request)).thenReturn(response);

        var httpResponse = pessoaController.atualizar(3L, request);

        verify(ownershipValidator).validarPropriedade(3L);
        verify(pessoaUseCase).atualizar(3L, request);
        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
    }

    @Test
    @DisplayName("deletar deve validar ownership e retornar 204")
    void deletar_deveValidarOwnershipERetornar204() {
        var httpResponse = pessoaController.deletar(4L);

        verify(ownershipValidator).validarPropriedade(4L);
        verify(pessoaUseCase).deletar(4L);
        assertEquals(HttpStatus.NO_CONTENT, httpResponse.getStatusCode());
    }

    @Test
    @DisplayName("login deve delegar e retornar 200")
    void login_deveDelegarERetornar200() {
        LoginRequest request = LoginRequest.builder().email("ana@x.com").senha("123456").build();
        TokenResponse response = TokenResponse.builder().id(1L).email("ana@x.com").token("jwt-token").build();
        when(pessoaUseCase.login(request)).thenReturn(response);

        var httpResponse = pessoaController.login(request);

        verify(pessoaUseCase).login(request);
        assertEquals(HttpStatus.OK, httpResponse.getStatusCode());
        assertEquals(response, httpResponse.getBody());
    }

    @Test
    @DisplayName("atualizarSenha deve validar ownership e retornar 204")
    void atualizarSenha_deveValidarOwnershipERetornar204() {
        SenhaRequest request = SenhaRequest.builder().senhaAtual("123").novaSenha("456").build();

        var httpResponse = pessoaController.atualizarSenha(8L, request);

        verify(ownershipValidator).validarPropriedade(8L);
        verify(pessoaUseCase).atualizarSenha(8L, request);
        assertEquals(HttpStatus.NO_CONTENT, httpResponse.getStatusCode());
    }
}

