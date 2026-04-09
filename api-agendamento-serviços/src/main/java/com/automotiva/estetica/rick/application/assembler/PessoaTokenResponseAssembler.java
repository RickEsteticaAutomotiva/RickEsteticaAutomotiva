package com.automotiva.estetica.rick.application.assembler;

import com.automotiva.estetica.rick.application.dto.response.TokenResponse;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import org.springframework.stereotype.Component;

@Component
public class PessoaTokenResponseAssembler {

    public TokenResponse toTokenResponse(Pessoa pessoa, String token) {
        return TokenResponse.builder().id(pessoa.getId()).email(pessoa.getEmail()).nome(pessoa.getNome()).token(token)
                .roles(pessoa.getRoles()).build();
    }
}
