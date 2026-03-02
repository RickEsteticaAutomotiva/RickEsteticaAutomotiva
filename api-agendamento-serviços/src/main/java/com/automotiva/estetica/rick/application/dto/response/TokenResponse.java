package com.automotiva.estetica.rick.application.dto.response;

import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private Long id;
    private String email;
    private String nome;
    private String token;
    /** Roles do usuário autenticado — útil para o front-end controlar menus/rotas. */
    private Set<RoleEnum> roles;
}
