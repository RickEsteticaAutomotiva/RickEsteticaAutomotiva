package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarServicoUseCase {

    private final BuscarServicoPorIdUseCase buscarServicoPorIdUseCase;
    private final ServicoGateway servicoGateway;

    public Servico execute(Long id, String nome, String descricao, BigDecimal preco, String imagem, Long categoriaId,
            Integer duracaoMinutos) {
        Servico servico = buscarServicoPorIdUseCase.execute(id);
        servico.atualizar(nome, descricao, preco, imagem, categoriaId, duracaoMinutos);
        return servicoGateway.salvar(servico);
    }
}
