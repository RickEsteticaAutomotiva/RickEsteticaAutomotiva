package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.assembler.ErroLogResponseAssembler;
import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.usecase.BuscarErroLogsComFiltrosUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarErroLogsPaginadosUseCase;
import com.automotiva.estetica.rick.domain.usecase.BuscarErroLogPorIdUseCase;
import com.automotiva.estetica.rick.domain.usecase.PurgarErroLogsAntigosUseCase;
import com.automotiva.estetica.rick.domain.usecase.RegistrarErroLogUseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ErroLogApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErroLogApplicationService.class);
    private static final int DIAS_RETENCAO = 90;

    private final RegistrarErroLogUseCase registrarErroLogUseCase;
    private final BuscarErroLogPorIdUseCase buscarErroLogPorIdUseCase;
    private final BuscarErroLogsPaginadosUseCase buscarErroLogsPaginadosUseCase;
    private final BuscarErroLogsComFiltrosUseCase buscarErroLogsComFiltrosUseCase;
    private final PurgarErroLogsAntigosUseCase purgarErroLogsAntigosUseCase;
    private final ErroLogResponseAssembler erroLogResponseAssembler;

    @Async("erroLogTaskExecutor")
    public void registrar(ErroLog erroLog) {
        try {
            registrarErroLogUseCase.execute(erroLog);
        } catch (Exception ex) {
            LOGGER.error("[ErroLogApplicationService] Falha ao persistir log de erro: {}", ex.getMessage(), ex);
        }
    }

    public ErroLogResponse buscarPorId(Long id) {
        ErroLog erroLog = buscarErroLogPorIdUseCase.execute(id);
        return erroLogResponseAssembler.toRedactedResponse(erroLog);
    }

    public Page<ErroLogResponse> buscarTodos(Pageable pageable) {
        return buscarErroLogsPaginadosUseCase.execute(pageable).map(erroLogResponseAssembler::toRedactedResponse);
    }

    public Page<ErroLogResponse> buscarComFiltros(String tipoExcecao, Integer statusHttp, String usuarioEmail,
            LocalDateTime de, LocalDateTime ate, Pageable pageable) {
        return buscarErroLogsComFiltrosUseCase.execute(tipoExcecao, statusHttp, usuarioEmail, de, ate, pageable)
                .map(erroLogResponseAssembler::toRedactedResponse);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgaLogAntigos() {
        LocalDateTime limite = LocalDateTime.now().minusDays(DIAS_RETENCAO);
        LOGGER.info("[ErroLogApplicationService] Iniciando purga de erros anteriores a {}", limite);
        purgarErroLogsAntigosUseCase.execute(limite);
        LOGGER.info("[ErroLogApplicationService] Purga concluida.");
    }

}
