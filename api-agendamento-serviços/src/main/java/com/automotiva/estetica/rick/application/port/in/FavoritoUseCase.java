package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.FavoritoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoFavoritoResponse;
import java.util.List;

public interface FavoritoUseCase {

    void adicionar(FavoritoRequest request);

    void remover(Long idFavorito);

    List<ServicoFavoritoResponse> listar(Long idPessoa);
}
