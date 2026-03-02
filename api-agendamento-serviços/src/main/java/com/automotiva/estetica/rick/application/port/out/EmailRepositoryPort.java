package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.Email;

public interface EmailRepositoryPort {

    Email salvar(Email email);
}
