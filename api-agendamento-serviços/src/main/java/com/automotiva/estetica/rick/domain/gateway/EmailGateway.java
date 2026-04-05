package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.Email;

public interface EmailGateway {

    void enviar(Email email);
}
