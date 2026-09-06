package com.automotiva.estetica.rick.application.controller;

import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.PessoaResponse;
import com.automotiva.estetica.rick.application.security.GerenteOnly;
import com.automotiva.estetica.rick.application.service.PessoaApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Busca de pessoas para uso exclusivo do gerente (ex.: matching de cliente na
 * importação de agendamento via IA). Endpoint separado de {@link PessoaController#buscarTodos}
 * — que continua {@code @ClienteOnly} — para não alterar a superfície de
 * acesso já existente; reaproveita 100% a mesma lógica de busca
 * ({@link PessoaApplicationService#buscarTodos}).
 */
@RestController
@RequestMapping("/pessoas-gestao")
@RequiredArgsConstructor
@GerenteOnly
@Tag(name = "Pessoas (Gestão)", description = "Busca de pessoas para uso exclusivo do gerente")
public class PessoaGestaoController {

    private final PessoaApplicationService pessoaUseCase;

    @GetMapping
    @Operation(summary = "Busca pessoas por filtro (nome/email/cpf) — uso do gerente")
    public ResponseEntity<Page<PessoaResponse>> buscarTodos(@Valid @ModelAttribute PageRequest pageRequest) {
        return ResponseEntity.ok(pessoaUseCase.buscarTodos(pageRequest));
    }
}
