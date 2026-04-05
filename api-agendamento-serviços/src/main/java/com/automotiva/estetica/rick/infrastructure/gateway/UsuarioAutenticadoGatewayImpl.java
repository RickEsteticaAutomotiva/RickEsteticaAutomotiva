package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import com.automotiva.estetica.rick.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioAutenticadoGatewayImpl implements UsuarioAutenticadoGateway {

    private final SecurityUtils securityUtils;

    @Override
    public Long obterIdUsuarioAutenticado() {
        return securityUtils.obterIdUsuarioAutenticado();
    }
}
