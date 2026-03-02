package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.response.CategoriaResponse;
import java.util.List;

public interface CategoriaUseCase {

    List<CategoriaResponse> buscarTodas();

    void criar(CategoriaRequest request);

    CategoriaResponse atualizar(Long id, CategoriaRequest request);
}
