package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.CarrinhoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoCarrinhoResponse;
import java.util.List;

public interface CarrinhoUseCase {

    void adicionar(CarrinhoRequest request);

    void remover(Long idCarrinho);

    void limparCarrinhoPessoa(Long idPessoa);

    List<ServicoCarrinhoResponse> listar(Long idPessoa);
}
