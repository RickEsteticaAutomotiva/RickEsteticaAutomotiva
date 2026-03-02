package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.response.ItemServicoResponse;
import java.util.List;

public interface ItemServicoUseCase {

    List<ItemServicoResponse> buscarTodos();

    ItemServicoResponse buscarPorId(Long id);

    List<ItemServicoResponse> listarPorOrdem(Long idOrdem);
}
