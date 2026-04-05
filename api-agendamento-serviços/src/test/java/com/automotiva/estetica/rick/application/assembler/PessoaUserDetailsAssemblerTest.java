package com.automotiva.estetica.rick.application.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.EnumSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PessoaUserDetailsAssemblerTest {

    private final PessoaUserDetailsAssembler assembler = new PessoaUserDetailsAssembler();

    @Test
    @DisplayName("Deve usar roles da pessoa para montar UserDetails")
    void toUserDetails_comRoles() {
        Pessoa pessoa = Pessoa.builder().email("admin@email.com").senha("hash")
                .roles(EnumSet.of(RoleEnum.ROLE_ADMIN, RoleEnum.ROLE_CLIENTE)).build();

        var userDetails = assembler.toUserDetails(pessoa);

        assertEquals("admin@email.com", userDetails.getUsername());
        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    @DisplayName("Deve aplicar fallback ROLE_CLIENTE quando roles vazio")
    void toUserDetails_semRoles_deveAplicarFallback() {
        Pessoa pessoa = Pessoa.builder().email("semrole@email.com").senha("hash").roles(EnumSet.noneOf(RoleEnum.class))
                .build();

        var userDetails = assembler.toUserDetails(pessoa);

        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_CLIENTE", userDetails.getAuthorities().iterator().next().getAuthority());
    }
}
