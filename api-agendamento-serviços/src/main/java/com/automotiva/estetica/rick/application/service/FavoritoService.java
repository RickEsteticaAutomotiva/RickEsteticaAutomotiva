package com.automotiva.estetica.rick.application.service;

import com.automotiva.estetica.rick.application.dto.request.FavoritoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoFavoritoResponse;
import com.automotiva.estetica.rick.application.port.in.FavoritoUseCase;
import com.automotiva.estetica.rick.application.port.out.FavoritoRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.PessoaRepositoryPort;
import com.automotiva.estetica.rick.application.port.out.ServicoRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoritoService implements FavoritoUseCase {

    private final FavoritoRepositoryPort favoritoRepositoryPort;
    private final PessoaRepositoryPort pessoaRepositoryPort;
    private final ServicoRepositoryPort servicoRepositoryPort;

    @Override
    public void adicionar(FavoritoRequest request) {
        Pessoa pessoa = pessoaRepositoryPort.buscarPorId(request.getIdPessoa())
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Usuário não encontrado: " + request.getIdPessoa()).detalhes("").build());
        Servico servico = servicoRepositoryPort.buscarPorId(request.getIdServico())
                .orElseThrow(() -> RecursoNaoEncontradoException.builder()
                        .mensagem("Serviço não encontrado: " + request.getIdServico()).detalhes("").build());

        if (favoritoRepositoryPort.existePorPessoaEServico(pessoa, servico)) {
            throw RecursoJaExisteException.builder().mensagem("Esse serviço já está nos favoritos deste usuário.")
                    .detalhes("").build();
        }

        favoritoRepositoryPort.salvar(Favorito.criar(pessoa, servico));
    }

    @Override
    public void remover(Long idFavorito) {
        favoritoRepositoryPort.buscarPorId(idFavorito).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("Favorito não encontrado.").detalhes("").build());
        favoritoRepositoryPort.deletarPorId(idFavorito);
    }

    @Override
    public List<ServicoFavoritoResponse> listar(Long idPessoa) {
        if (!pessoaRepositoryPort.existePorId(idPessoa)) {
            return List.of();
        }
        return favoritoRepositoryPort.buscarPorPessoaId(idPessoa).stream()
                .map(f -> ServicoFavoritoResponse.builder().idFavorito(f.getId()).idServico(f.getServico().getId())
                        .nome(f.getServico().getNome()).descricao(f.getServico().getDescricao())
                        .preco(f.getServico().getPreco()).imagem(f.getServico().getImagem()).build())
                .toList();
    }
}
