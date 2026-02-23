package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import java.util.List;

public interface VeiculoUseCase {

    VeiculoResponse cadastrar(VeiculoRequest request);

    List<VeiculoResponse> buscarTodos();

    List<VeiculoResponse> buscarPorPessoaId(Long pessoaId);

    void atualizar(Long id, VeiculoRequest request);

    void deletar(Long id);
}
