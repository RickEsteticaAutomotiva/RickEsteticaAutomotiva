package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.application.port.in.ErroLogUseCase;
import com.automotiva.estetica.rick.application.port.out.ErroLogRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
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

/**
 * Service de Log de Erros.
 *
 * <p>
 * registrar() é @Async para nunca bloquear o response HTTP ao cliente.
 * purgaLogAntigos() roda toda madrugada e remove registros com mais de 90 dias.
 */
@Service
@RequiredArgsConstructor
public class ErroLogService implements ErroLogUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErroLogService.class);
    private static final int DIAS_RETENCAO = 90;

    private final ErroLogRepositoryPort erroLogRepositoryPort;

    @Override
    @Async("erroLogTaskExecutor")
    public void registrar(ErroLog erroLog) {
        try {
            erroLogRepositoryPort.salvar(erroLog);
        } catch (Exception ex) {
            // Nunca lançar exceção aqui — não pode interferir no response principal
            LOGGER.error("[ErroLogService] Falha ao persistir log de erro: {}", ex.getMessage(), ex);
        }
    }

    @Override
    public ErroLogResponse buscarPorId(Long id) {
        return erroLogRepositoryPort.buscarPorId(id).map(this::toResponse)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Log de erro não encontrado com o ID: " + id)
                        .detalhes("Verifique se o ID informado existe na tabela erro_log").build());
    }

    @Override
    public Page<ErroLogResponse> buscarTodos(Pageable pageable) {
        return erroLogRepositoryPort.buscarTodos(pageable).map(this::toResponse);
    }

    @Override
    public Page<ErroLogResponse> buscarComFiltros(String tipoExcecao, Integer statusHttp, String usuarioEmail,
            LocalDateTime de, LocalDateTime ate, Pageable pageable) {

        return erroLogRepositoryPort.buscarComFiltros(tipoExcecao, statusHttp, usuarioEmail, de, ate, pageable)
                .map(this::toResponse);
    }

    /**
     * Purga automática de logs antigos. Executa toda madrugada às 03:00 e remove
     * registros com mais de 90 dias. Configurável via propriedade:
     * app.erro-log.retencao-dias
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgaLogAntigos() {
        LocalDateTime limite = LocalDateTime.now().minusDays(DIAS_RETENCAO);
        LOGGER.info("[ErroLogService] Iniciando purga de erros anteriores a {}", limite);
        erroLogRepositoryPort.deletarAnterioresA(limite);
        LOGGER.info("[ErroLogService] Purga concluída.");
    }

    // -------------------------------------------------------------------------
    // Mapeamento interno domain → DTO
    // -------------------------------------------------------------------------

    private ErroLogResponse toResponse(ErroLog e) {
        return ErroLogResponse.builder().id(e.getId()).timestamp(e.getTimestamp()).tipoExcecao(e.getTipoExcecao())
                .mensagem(e.getMensagem()).stackTrace(e.getStackTrace()).endpoint(e.getEndpoint())
                .metodoHttp(e.getMetodoHttp()).payloadRequisicao(e.getPayloadRequisicao())
                .queryParams(e.getQueryParams()).headersRequisicao(e.getHeadersRequisicao())
                .usuarioEmail(e.getUsuarioEmail()).statusHttp(e.getStatusHttp()).ambiente(e.getAmbiente())
                .ipCliente(e.getIpCliente()).userAgent(e.getUserAgent()).build();
    }
}
