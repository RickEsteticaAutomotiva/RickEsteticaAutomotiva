package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.gateway.ErroLogGateway;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarErroLogsComFiltrosUseCase {

    private final ErroLogGateway erroLogGateway;

    public Page<ErroLog> execute(String tipoExcecao, Integer statusHttp, String usuarioEmail, LocalDateTime de,
            LocalDateTime ate, Pageable pageable) {
        return erroLogGateway.buscarComFiltros(tipoExcecao, statusHttp, usuarioEmail, de, ate, pageable);
    }
}
