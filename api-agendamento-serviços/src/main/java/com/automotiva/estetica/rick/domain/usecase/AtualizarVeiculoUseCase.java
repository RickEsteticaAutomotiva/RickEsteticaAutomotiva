package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarVeiculoUseCase {

    private final VeiculoGateway veiculoGateway;

    public Veiculo execute(Long id, String placa, String modelo, String marca, String porte, String cor, String ano) {
        Veiculo veiculo = veiculoGateway.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("Veiculo com id " + id + " não foi encontrado").detalhes("").build());

        veiculo.atualizar(placa, modelo, marca, porte, cor, ano);
        return veiculoGateway.salvar(veiculo);
    }
}
