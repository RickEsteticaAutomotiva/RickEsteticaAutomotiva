package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Use case para listar serviços paginados com cache.
 *
 * <p>
 * O resultado é cacheado em Redis com TTL de 10 minutos. A chave do cache é
 * composta por filtro + page + size + sort, garantindo que cada combinação de
 * parâmetros de paginação tenha seu próprio cache.
 *
 * <p>
 * O cache é invalidado automaticamente quando serviços são criados, atualizados
 * ou deletados (via @CacheEvict nos respectivos use cases).
 */
@Service
@RequiredArgsConstructor
public class ListarServicosUseCase {

    private final ServicoGateway servicoGateway;

    /**
     * Lista serviços paginados com filtro opcional, com resultado cacheado.
     *
     * @param filtro
     *            filtro de busca (nome/descrição); null ou vazio ignora
     * @param pageable
     *            informações de paginação (page, size, sort)
     * @return página de serviços
     */
    @Cacheable(cacheNames = "servicos", key = "(#filtro != null ? #filtro : 'null') + ':' + #pageable.getPageNumber() + ':' + #pageable.getPageSize()", unless = "#result == null")
    public Page<Servico> execute(String filtro, Pageable pageable) {
        return servicoGateway.buscarTodos(filtro, pageable);
    }
}
