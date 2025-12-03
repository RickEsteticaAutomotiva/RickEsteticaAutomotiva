package com.automotiva.estetica.rick.api_agendamento_servicos.config;

import com.automotiva.estetica.rick.api_agendamento_servicos.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticacaoProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final AutenticacaoService autenticacaoService;

    @Autowired
    public AutenticacaoProvider(PasswordEncoder passwordEncoder, AutenticacaoService autenticacaoService) {
        this.passwordEncoder = passwordEncoder;
        this.autenticacaoService = autenticacaoService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String senha = authentication.getCredentials().toString();

        UserDetails userDetails = this.autenticacaoService.loadUserByUsername(username);

        if (!passwordEncoder.matches(senha, userDetails.getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
