package com.automotiva.estetica.rick.application.dto.response;

/**
 * DTO de saída da porta OrdemServicoRepositoryPort.buscarCancelamentosPorMotivoDoPeriodo.
 */
public record CancelamentoMotivoDto(String tipo, Long quantidade) {}

