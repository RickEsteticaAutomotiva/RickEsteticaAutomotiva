package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * Use case para atualizar serviço existente.
 *
 * <p>
 * Invalida o cache "servicos" ao modificar um serviço, garantindo que a próxima
 * chamada a ListarServicosUseCase buscará dados atualizados do BD.
 */
@Service
@RequiredArgsConstructor
public class AtualizarServicoUseCase {

    private final BuscarServicoPorIdUseCase buscarServicoPorIdUseCase;
    private final ServicoGateway servicoGateway;

    /**
     * Atualiza serviço existente e invalida cache.
     *
     * @param id
     *            identificador do serviço
     * @param nome
     *            novo nome
     * @param descricao
     *            nova descrição
     * @param preco
     *            novo preço
     * @param imagem
     *            nova URL de imagem
     * @param categoriaId
     *            novo ID de categoria
     * @param duracaoMinutos
     *            nova duração em minutos
     * @return serviço atualizado
     */
    @CacheEvict(cacheNames = "servicos", allEntries = true)
    public Servico execute(Long id, String nome, String descricao, BigDecimal preco, String imagem, Long categoriaId,
            Integer duracaoMinutos) {
        Servico servico = buscarServicoPorIdUseCase.execute(id);
        servico.atualizar(nome, descricao, preco, imagem, categoriaId, duracaoMinutos);
        return servicoGateway.salvar(servico);
    }
}
