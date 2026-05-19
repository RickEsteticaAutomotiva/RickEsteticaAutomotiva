package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * Use case para cadastrar novo serviço.
 *
 * <p>
 * Invalida o cache "servicos" ao criar um novo serviço, garantindo que a
 * próxima chamada a ListarServicosUseCase buscará dados atualizados do BD.
 */
@Service
@RequiredArgsConstructor
public class CadastrarServicoUseCase {

    private final ServicoGateway servicoGateway;

    /**
     * Cria novo serviço e invalida cache.
     *
     * @param servico
     *            objeto com dados do serviço
     * @return serviço persistido com ID gerado
     */
    @CacheEvict(cacheNames = "servicos", allEntries = true)
    public Servico execute(Servico servico) {
        return servicoGateway.salvar(servico);
    }
}
