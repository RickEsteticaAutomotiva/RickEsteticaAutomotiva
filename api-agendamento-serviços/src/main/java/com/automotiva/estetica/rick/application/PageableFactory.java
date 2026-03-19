package com.automotiva.estetica.rick.application;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoGestaoPageRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utilitário de paginação da camada de aplicação. Centraliza a construção do
 * Pageable a partir do PageRequest customizado, eliminando o bloco duplicado
 * presente em múltiplos services.
 */
public final class PageableFactory {

    private PageableFactory() {
    }

    /**
     * Constrói um {@link Pageable} a partir do {@link PageRequest} da aplicação. Se
     * o campo ordenarPor não for informado, ordena por "id" como padrão.
     *
     * @param pageRequest
     *            DTO com parâmetros de paginação
     * @return Pageable pronto para uso no repositório
     */
    public static Pageable from(PageRequest pageRequest) {
        String ordenarPor = (pageRequest.getOrdenarPor() == null || pageRequest.getOrdenarPor().isBlank())
                ? "id"
                : pageRequest.getOrdenarPor();

        String[] campos = ordenarPor.split(",");
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }

        return org.springframework.data.domain.PageRequest.of(pageRequest.getPagina(), pageRequest.getTamanho(),
                Sort.by(campos));
    }

    /**
     * Constrói um {@link Pageable} a partir do {@link OrdemServicoGestaoPageRequest} da aplicação.
     * Se o campo ordenarPor não for informado, ordena por "dataAgendamento" como padrão. Se a
     * direção não for informada, considera "desc" como padrão.
     *
     * @param pageRequest DTO com parâmetros de paginação para gestão de ordens
     * @return Pageable pronto para uso no repositório
     */
    public static Pageable from(OrdemServicoGestaoPageRequest pageRequest) {
        String ordenarPor =
                (pageRequest.getOrdenarPor() == null || pageRequest.getOrdenarPor().isBlank())
                        ? "dataAgendamento"
                        : pageRequest.getOrdenarPor();

        String direcao =
                (pageRequest.getDirecao() == null || pageRequest.getDirecao().isBlank())
                        ? "desc"
                        : pageRequest.getDirecao();

        Sort.Direction sortDirection =
                "asc".equalsIgnoreCase(direcao) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String[] campos = ordenarPor.split(",");
        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPagina(), pageRequest.getTamanho(), Sort.by(sortDirection, campos));
    }
}
