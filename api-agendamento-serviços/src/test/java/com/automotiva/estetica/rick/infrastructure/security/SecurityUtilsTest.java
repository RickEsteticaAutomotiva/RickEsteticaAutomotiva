package com.automotiva.estetica.rick.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de SecurityUtils")
class SecurityUtilsTest {

    @Mock
    private PessoaGateway pessoaGateway;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("obterIdUsuarioAutenticado deve retornar id quando usuario existir")
    void obterIdUsuarioAutenticado_deveRetornarId() {
        SecurityUtils securityUtils = new SecurityUtils(pessoaGateway);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "cliente@rick.com", "senha", List.of()));
        when(pessoaGateway.buscarPorEmail("cliente@rick.com")).thenReturn(Optional.of(Pessoa.builder().id(77L).build()));

        Long idUsuario = securityUtils.obterIdUsuarioAutenticado();

        assertEquals(77L, idUsuario);
    }

    @Test
    @DisplayName("obterIdUsuarioAutenticado deve falhar sem autenticacao")
    void obterIdUsuarioAutenticado_deveFalharSemAutenticacao() {
        SecurityUtils securityUtils = new SecurityUtils(pessoaGateway);
        SecurityContextHolder.clearContext();

        assertThrows(AcessoNegadoException.class, securityUtils::obterIdUsuarioAutenticado);
    }

    @Test
    @DisplayName("obterIdUsuarioAutenticado deve falhar quando usuario nao existir")
    void obterIdUsuarioAutenticado_deveFalharQuandoUsuarioNaoExistir() {
        SecurityUtils securityUtils = new SecurityUtils(pessoaGateway);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "inexistente@rick.com", "senha", List.of()));
        when(pessoaGateway.buscarPorEmail("inexistente@rick.com")).thenReturn(Optional.empty());

        assertThrows(AcessoNegadoException.class, securityUtils::obterIdUsuarioAutenticado);
    }

    @Test
    @DisplayName("obterUsernameAutenticado deve retornar username autenticado")
    void obterUsernameAutenticado_deveRetornarUsername() {
        SecurityUtils securityUtils = new SecurityUtils(pessoaGateway);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "gerente@rick.com", "senha", List.of()));

        String username = securityUtils.obterUsernameAutenticado();

        assertEquals("gerente@rick.com", username);
    }

    @Test
    @DisplayName("obterUsernameAutenticado deve falhar sem autenticacao")
    void obterUsernameAutenticado_deveFalharSemAutenticacao() {
        SecurityUtils securityUtils = new SecurityUtils(pessoaGateway);
        SecurityContextHolder.clearContext();

        assertThrows(AcessoNegadoException.class, securityUtils::obterUsernameAutenticado);
    }
}

