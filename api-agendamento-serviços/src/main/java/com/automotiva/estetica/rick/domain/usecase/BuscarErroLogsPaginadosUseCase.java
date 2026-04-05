package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.gateway.ErroLogGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarErroLogsPaginadosUseCase {

    private final ErroLogGateway erroLogGateway;

    public Page<ErroLog> execute(Pageable pageable) {
        return erroLogGateway.buscarTodos(pageable);
    }
}
