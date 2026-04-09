package com.automotiva.estetica.rick.application.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.EnumSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PessoaTokenResponseAssemblerTest {

    private final PessoaTokenResponseAssembler assembler = new PessoaTokenResponseAssembler();

    @Test
    @DisplayName("Deve montar TokenResponse com dados da pessoa e token")
    void toTokenResponse_sucesso() {
        Pessoa pessoa = Pessoa.builder().id(9L).email("cliente@email.com").nome("Cliente")
                .roles(EnumSet.of(RoleEnum.ROLE_CLIENTE)).build();

        var response = assembler.toTokenResponse(pessoa, "jwt.token");

        assertEquals(9L, response.getId());
        assertEquals("cliente@email.com", response.getEmail());
        assertEquals("Cliente", response.getNome());
        assertEquals("jwt.token", response.getToken());
        assertEquals(EnumSet.of(RoleEnum.ROLE_CLIENTE), response.getRoles());
    }
}
