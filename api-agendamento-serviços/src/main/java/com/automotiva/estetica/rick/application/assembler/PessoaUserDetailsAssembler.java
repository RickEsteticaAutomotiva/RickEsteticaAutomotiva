package com.automotiva.estetica.rick.application.assembler;

import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class PessoaUserDetailsAssembler {

    public UserDetails toUserDetails(Pessoa pessoa) {
        Set<RoleEnum> roles = (pessoa.getRoles() != null && !pessoa.getRoles().isEmpty())
                ? pessoa.getRoles()
                : EnumSet.of(RoleEnum.ROLE_CLIENTE);

        var authorities = roles.stream().map(role -> role.authority())
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(pessoa.getEmail(), pessoa.getSenha(), authorities);
    }
}
