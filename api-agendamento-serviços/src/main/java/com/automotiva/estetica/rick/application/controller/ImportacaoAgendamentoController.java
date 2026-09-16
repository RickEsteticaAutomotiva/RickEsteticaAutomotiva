package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.ExtrairAgendamentoRequest;
import com.automotiva.estetica.rick.application.dto.response.ImportarAgendamentoResponse;
import com.automotiva.estetica.rick.application.security.GerenteOnly;
import com.automotiva.estetica.rick.application.service.ImportacaoAgendamentoApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Extrai dados de agendamento a partir de uma foto (anotação, print de
 * WhatsApp) via Gemini multimodal e faz o matching com registros reais
 * (pessoa, veículo, serviço) para revisão do gerente. Não cria nada — a
 * criação da Ordem de Serviço acontece via {@code POST /ordem-servicos-gestao}
 * já existente, somente após confirmação explícita do gerente com os dados
 * revisados/corrigidos.
 */
@RestController
@RequestMapping("/ordem-servicos-gestao/importar-ia")
@RequiredArgsConstructor
@GerenteOnly
@Tag(name = "Importação de agendamento via IA", description = "Extração e matching de agendamentos a partir de foto, para revisão do gerente")
public class ImportacaoAgendamentoController {

    private final ImportacaoAgendamentoApplicationService importacaoAgendamentoApplicationService;

    @PostMapping
    @Operation(summary = "Extrai dados de um agendamento a partir de uma foto e retorna candidatos de matching para revisão")
    public ResponseEntity<ImportarAgendamentoResponse> importar(@Valid @RequestBody ExtrairAgendamentoRequest request) {
        return ResponseEntity.ok(importacaoAgendamentoApplicationService.importar(request));
    }
}
