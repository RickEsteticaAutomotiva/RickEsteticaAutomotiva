package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.application.service.ErroLogApplicationService;
import com.automotiva.estetica.rick.application.security.AdminOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para consulta do log de erros em runtime.
 *
 * <p>
 * Protegido por role ADMIN — apenas administradores podem visualizar os logs.
 *
 * <p>
 * Camada: application/controller.
 */
@RestController
@RequestMapping("/erros-log")
@RequiredArgsConstructor
@AdminOnly
@Tag(name = "Erros Log", description = "Consulta e análise de erros registrados em runtime (somente ADMIN)")
public class ErroLogController {

    private final ErroLogApplicationService erroLogUseCase;

    @GetMapping
    @Operation(summary = "Lista todos os erros paginados", description = "Retorna todos os erros registrados, ordenados do mais recente para o mais antigo.")
    public ResponseEntity<Page<ErroLogResponse>> buscarTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp") Pageable pageable) {
        return ResponseEntity.ok(erroLogUseCase.buscarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um erro por ID", description = "Retorna todos os detalhes do erro, incluindo stack trace e payload da requisição.")
    public ResponseEntity<ErroLogResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(erroLogUseCase.buscarPorId(id));
    }

    @GetMapping("/filtros")
    @Operation(summary = "Busca erros com filtros", description = "Filtra erros por tipo de exceção, status HTTP, usuário, "
            + "e intervalo de datas (formato ISO: yyyy-MM-ddTHH:mm:ss).")
    public ResponseEntity<Page<ErroLogResponse>> buscarComFiltros(@RequestParam(required = false) String tipoExcecao,
            @RequestParam(required = false) Integer statusHttp, @RequestParam(required = false) String usuarioEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate,
            @ParameterObject @PageableDefault(size = 20, sort = "timestamp") Pageable pageable) {

        return ResponseEntity
                .ok(erroLogUseCase.buscarComFiltros(tipoExcecao, statusHttp, usuarioEmail, de, ate, pageable));
    }
}
