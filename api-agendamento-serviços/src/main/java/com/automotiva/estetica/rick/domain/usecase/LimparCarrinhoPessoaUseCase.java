package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.CarrinhoGateway;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LimparCarrinhoPessoaUseCase {

    private final CarrinhoGateway carrinhoGateway;
    private final PessoaGateway pessoaGateway;
    private final UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    public void execute(Long idPessoa) {
        validarOwnership(idPessoa);

        pessoaGateway.buscarPorId(idPessoa).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("Usuário não encontrado: " + idPessoa).detalhes("").build());

        List<Carrinho> itens = carrinhoGateway.buscarPorPessoaId(idPessoa);
        if (itens != null && !itens.isEmpty()) {
            carrinhoGateway.deletarTodos(itens);
        }
    }

    private void validarOwnership(Long idPessoa) {
        Long usuarioId = usuarioAutenticadoGateway.obterIdUsuarioAutenticado();
        if (!usuarioId.equals(idPessoa)) {
            throw AcessoNegadoException.builder().mensagem("você não tem permissão para acessar este recurso")
                    .detalhes("tentativa de operação no carrinho de outro usuário").build();
        }
    }
}
