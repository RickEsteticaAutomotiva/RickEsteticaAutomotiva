package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.CarrinhoGateway;
import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoverCarrinhoUseCase {

    private final CarrinhoGateway carrinhoGateway;
    private final UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    public void execute(Long idCarrinho) {
        Carrinho carrinho = carrinhoGateway.buscarPorId(idCarrinho).orElseThrow(() -> RecursoNaoEncontradoException
                .builder().mensagem("Item do carrinho não encontrado.").detalhes("").build());

        validarOwnership(carrinho.getPessoa().getId());
        carrinhoGateway.deletarPorId(idCarrinho);
    }

    private void validarOwnership(Long idPessoaDona) {
        Long usuarioId = usuarioAutenticadoGateway.obterIdUsuarioAutenticado();
        if (!usuarioId.equals(idPessoaDona)) {
            throw AcessoNegadoException.builder().mensagem("você não tem permissão para acessar este recurso")
                    .detalhes("tentativa de operação no carrinho de outro usuário").build();
        }
    }
}
