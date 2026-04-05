package com.automotiva.estetica.rick.application.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de OwnershipValidator")
class OwnershipValidatorTest {

    @Mock
    private UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    @InjectMocks
    private OwnershipValidator ownershipValidator;

    @Test
    @DisplayName("validarPropriedade deve permitir quando recurso pertence ao usuario")
    void validarPropriedade_quandoMesmoUsuario_naoDeveLancar() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(10L);

        assertDoesNotThrow(() -> ownershipValidator.validarPropriedade(10L));
    }

    @Test
    @DisplayName("validarPropriedade deve negar quando recurso pertence a outro usuario")
    void validarPropriedade_quandoOutroUsuario_deveLancarAcessoNegado() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(10L);

        assertThrows(AcessoNegadoException.class, () -> ownershipValidator.validarPropriedade(20L));
    }

    @Test
    @DisplayName("validarPropriedadePessoa deve permitir quando dono e usuario autenticado coincidem")
    void validarPropriedadePessoa_quandoMesmoDono_naoDeveLancar() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(5L);

        assertDoesNotThrow(() -> ownershipValidator.validarPropriedadePessoa(99L, 5L));
    }

    @Test
    @DisplayName("validarPropriedadePessoa deve negar quando dono diverge do usuario autenticado")
    void validarPropriedadePessoa_quandoDonoDiverge_deveLancarAcessoNegado() {
        when(usuarioAutenticadoGateway.obterIdUsuarioAutenticado()).thenReturn(5L);

        assertThrows(AcessoNegadoException.class, () -> ownershipValidator.validarPropriedadePessoa(99L, 7L));
    }
}

