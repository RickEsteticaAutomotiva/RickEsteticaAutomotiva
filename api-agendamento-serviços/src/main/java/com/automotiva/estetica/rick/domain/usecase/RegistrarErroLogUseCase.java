package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.gateway.ErroLogGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrarErroLogUseCase {

    private final ErroLogGateway erroLogGateway;

    public ErroLog execute(ErroLog erroLog) {
        return erroLogGateway.salvar(erroLog);
    }
}
