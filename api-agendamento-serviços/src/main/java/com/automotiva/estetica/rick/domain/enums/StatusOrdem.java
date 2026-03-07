package com.automotiva.estetica.rick.domain.enums;

import java.util.Arrays;

/**
 * Enumeração dos status possíveis de uma OrdemServico. Centraliza os IDs de status para eliminar
 * magic numbers no domínio.
 */
public enum StatusOrdem {
    AGUARDANDO(1L),
    EM_ANDAMENTO(2L),
    AGUARDANDO_PECAS(3L),
    CANCELADO(4L),
    CONCLUIDO(5L);

    private final Long id;

    StatusOrdem(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /**
     * Busca o enum correspondente ao id fornecido.
     *
     * @param id identificador do status
     * @return StatusOrdem correspondente, ou null se não encontrado
     */
    public static StatusOrdem fromId(Long id) {
        if (id == null) return null;
        return Arrays.stream(values()).filter(s -> s.id.equals(id)).findFirst().orElse(null);
    }

    /**
     * Regra de domínio: indica se o status requer notificação por e-mail ao cliente.
     *
     * @param statusId id do status a verificar
     * @return true se o status for EM_ANDAMENTO ou CONCLUIDO
     */
    public static boolean requerNotificacao(Long statusId) {
        StatusOrdem s = fromId(statusId);
        return s == EM_ANDAMENTO || s == CONCLUIDO;
    }
}
