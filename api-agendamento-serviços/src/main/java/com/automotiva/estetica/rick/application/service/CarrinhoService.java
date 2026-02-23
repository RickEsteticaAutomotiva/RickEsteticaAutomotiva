package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.request.CarrinhoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoCarrinhoResponse;
import com.automotiva.estetica.rick.application.port.in.CarrinhoUseCase;
import com.automotiva.estetica.rick.application.port.out.CarrinhoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarrinhoService implements CarrinhoUseCase {

    private final CarrinhoRepositoryPort carrinhoRepositoryPort;
    private final PessoaRepositoryPort pessoaRepositoryPort;
    private final ServicoRepositoryPort servicoRepositoryPort;

    @Override
    public void adicionar(CarrinhoRequest request) {
        Pessoa pessoa = pessoaRepositoryPort
                .buscarPorId(request.getIdPessoa())
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Usuário não encontrado: " + request.getIdPessoa())
                        .detalhes("")
                        .build());
        Servico servico = servicoRepositoryPort
                .buscarPorId(request.getIdServico())
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Serviço não encontrado: " + request.getIdServico())
                        .detalhes("")
                        .build());

        if (carrinhoRepositoryPort.existePorPessoaEServico(pessoa, servico)) {
            throw RecursoJaExisteException.builder()
                    .mensagem("Esse serviço já está no carrinho deste usuário.")
                    .detalhes("")
                    .build();
        }

        Carrinho carrinho = Carrinho.criar(pessoa, servico);
        carrinhoRepositoryPort.salvar(carrinho);
    }

    @Override
    @Transactional
    public void remover(Long idCarrinho) {
        carrinhoRepositoryPort.buscarPorId(idCarrinho).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("Item do carrinho não encontrado.")
                .detalhes("")
                .build());
        carrinhoRepositoryPort.deletarPorId(idCarrinho);
    }

    @Override
    @Transactional
    public void limparCarrinhoPessoa(Long idPessoa) {
        Pessoa pessoa = pessoaRepositoryPort
                .buscarPorId(idPessoa)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Usuário não encontrado: " + idPessoa)
                        .detalhes("")
                        .build());

        List<Carrinho> itens = carrinhoRepositoryPort.buscarPorPessoaId(pessoa.getId());
        if (itens != null && !itens.isEmpty()) {
            carrinhoRepositoryPort.deletarTodos(itens);
        }
    }

    @Override
    public List<ServicoCarrinhoResponse> listar(Long idPessoa) {
        Pessoa pessoa = pessoaRepositoryPort
                .buscarPorId(idPessoa)
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Usuário não encontrado: " + idPessoa)
                        .detalhes("")
                        .build());

        return carrinhoRepositoryPort.buscarPorPessoaId(pessoa.getId()).stream()
                .map(c -> ServicoCarrinhoResponse.builder()
                        .idCarrinho(c.getId())
                        .idServico(c.getServico().getId())
                        .nome(c.getServico().getNome())
                        .descricao(c.getServico().getDescricao())
                        .preco(c.getServico().getPreco())
                        .imagem(c.getServico().getImagem())
                        .build())
                .toList();
    }
}
