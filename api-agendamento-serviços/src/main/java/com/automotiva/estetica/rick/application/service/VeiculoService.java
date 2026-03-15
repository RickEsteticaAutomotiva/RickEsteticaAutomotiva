package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import com.automotiva.estetica.rick.application.port.in.VeiculoUseCase;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.VeiculoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VeiculoService implements VeiculoUseCase {

    private final VeiculoRepositoryPort veiculoRepositoryPort;
    private final PessoaRepositoryPort pessoaRepositoryPort;

    @Override
    public VeiculoResponse cadastrar(VeiculoRequest request) {
        Pessoa pessoa = pessoaRepositoryPort.buscarPorId(request.getIdPessoa())
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("a pessoa com id " + request.getIdPessoa() + " não foi encontrada").detalhes("")
                        .build());

        Veiculo veiculo = Veiculo.builder().placa(request.getPlaca()).modelo(request.getModelo())
                .marca(request.getMarca()).porte(request.getPorte()).cor(request.getCor()).ano(request.getAno())
                .pessoa(pessoa).build();

        return toResponse(veiculoRepositoryPort.salvar(veiculo));
    }

    @Override
    public List<VeiculoResponse> buscarTodos() {
        return veiculoRepositoryPort.buscarTodos().stream().map(this::toResponse).toList();
    }

    @Override
    public List<VeiculoResponse> buscarPorPessoaId(Long pessoaId) {
        if (!pessoaRepositoryPort.existePorId(pessoaId)) {
            return List.of();
        }
        return veiculoRepositoryPort.buscarPorPessoaId(pessoaId).stream().map(this::toResponse).toList();
    }

    @Override
    public void atualizar(Long id, VeiculoRequest request) {
        Veiculo veiculo = veiculoRepositoryPort.buscarPorId(id).orElseThrow(() -> RecursoNaoEncontradoException
                .builder().mensagem("o veículo com id " + id + " não foi encontrado").detalhes("").build());

        veiculo.atualizar(request.getPlaca(), request.getModelo(), request.getMarca(), request.getPorte(),
                request.getCor(), request.getAno());

        veiculoRepositoryPort.salvar(veiculo);
    }

    @Override
    public void deletar(Long id) {
        if (!veiculoRepositoryPort.existePorId(id)) {
            throw RecursoNaoEncontradoException.builder().mensagem("o veículo com id " + id + " não foi encontrado")
                    .detalhes("").build();
        }
        veiculoRepositoryPort.deletarPorId(id);
    }

    private VeiculoResponse toResponse(Veiculo v) {
        return VeiculoResponse.builder().id(v.getId()).placa(v.getPlaca()).modelo(v.getModelo()).marca(v.getMarca())
                .porte(v.getPorte()).cor(v.getCor()).ano(v.getAno())
                .idPessoa(v.getPessoa() != null ? v.getPessoa().getId() : null).build();
    }
}
