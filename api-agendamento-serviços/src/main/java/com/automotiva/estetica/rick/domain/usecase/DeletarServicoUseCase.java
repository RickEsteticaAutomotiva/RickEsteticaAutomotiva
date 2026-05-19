package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * Use case para deletar serviço.
 *
 * <p>
 * Invalida o cache "servicos" ao remover um serviço, garantindo que a próxima
 * chamada a ListarServicosUseCase buscará dados atualizados do BD.
 */
@Service
@RequiredArgsConstructor
public class DeletarServicoUseCase {

    private final ServicoGateway servicoGateway;

    /**
     * Deleta serviço existente e invalida cache.
     *
     * @param id
     *            identificador do serviço
     * @throws RecursoNaoEncontradoException
     *             se serviço não existe
     */
    @CacheEvict(cacheNames = "servicos", allEntries = true)
    public void execute(Long id) {
        if (!servicoGateway.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder().mensagem("o serviço com id " + id + " não foi encontrado")
                    .detalhes("").build();
        }
        servicoGateway.deletarPorId(id);
    }
}
