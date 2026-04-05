package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarVeiculosPorPessoaUseCase {

    private final VeiculoGateway veiculoGateway;
    private final PessoaGateway pessoaGateway;

    public List<Veiculo> execute(Long pessoaId) {
        if (!pessoaGateway.existePorId(pessoaId)) {
            return List.of();
        }
        return veiculoGateway.buscarPorPessoaId(pessoaId);
    }
}
