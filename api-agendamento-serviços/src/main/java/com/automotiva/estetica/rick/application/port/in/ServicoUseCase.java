package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import org.springframework.data.domain.Page;

public interface ServicoUseCase {

    Page<ServicoResponse> buscarTodos(PageRequest pageRequest);

    ServicoResponse buscarPorId(Long id);

    ServicoResponse criar(ServicoRequest request);

    ServicoResponse atualizar(Long id, ServicoRequest request);

    void deletar(Long id);
}
