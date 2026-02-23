package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface OrdemServicoUseCase {

    Page<OrdemServicoResponse> buscarTodos(PageRequest pageRequest);

    OrdemServicoResponse buscarPorId(Long id);

    List<OrdemServicoResponse> buscarPorUsuarioId(Long usuarioId);

    OrdemServicoResponse criar(OrdemServicoRequest request);

    OrdemServicoResponse atualizar(Long id, OrdemServicoRequest request);

    void deletar(Long id);
}
