package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ErroLogGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarErroLogPorIdUseCase {

    private final ErroLogGateway erroLogGateway;

    public ErroLog execute(Long id) {
        return erroLogGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("ErroLog com id " + id + " não foi encontrado").detalhes("").build());
    }
}
