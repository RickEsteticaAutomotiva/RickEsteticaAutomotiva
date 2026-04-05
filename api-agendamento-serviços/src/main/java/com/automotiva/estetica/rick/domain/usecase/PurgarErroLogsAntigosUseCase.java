package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.gateway.ErroLogGateway;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurgarErroLogsAntigosUseCase {

    private final ErroLogGateway erroLogGateway;

    public void execute(LocalDateTime dataLimite) {
        erroLogGateway.deletarAnterioresA(dataLimite);
    }
}
