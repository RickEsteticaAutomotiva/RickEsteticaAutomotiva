package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Porta de entrada (Use Case) para o log de erros.
 *
 * <p>registrar() é invocado assincronamente pelo GlobalExceptionHandler para cada erro capturado.
 * Os demais métodos são usados pelo ErroLogController para consulta e análise.
 *
 * <p>Camada: application/port/in.
 */
public interface ErroLogUseCase {

    /**
     * Persiste um erro capturado em runtime. Chamado de forma assíncrona — nunca deve lançar
     * exceção.
     */
    void registrar(ErroLog erroLog);

    /** Busca um log de erro pelo identificador único. */
    ErroLogResponse buscarPorId(Long id);

    /** Lista todos os logs de erro paginados, ordenados do mais recente ao mais antigo. */
    Page<ErroLogResponse> buscarTodos(Pageable pageable);

    /**
     * Lista logs com filtros opcionais: tipo de exceção, status HTTP, e-mail do usuário e intervalo
     * de datas.
     */
    Page<ErroLogResponse> buscarComFiltros(
            String tipoExcecao,
            Integer statusHttp,
            String usuarioEmail,
            LocalDateTime de,
            LocalDateTime ate,
            Pageable pageable);
}
