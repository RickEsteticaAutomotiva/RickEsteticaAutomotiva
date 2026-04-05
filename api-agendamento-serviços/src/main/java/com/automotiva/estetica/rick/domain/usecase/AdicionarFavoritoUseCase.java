package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.exception.RecursoJaExisteException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.FavoritoGateway;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdicionarFavoritoUseCase {

    private final FavoritoGateway favoritoGateway;
    private final PessoaGateway pessoaGateway;
    private final ServicoGateway servicoGateway;
    private final UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    public void execute(Long idPessoa, Long idServico) {
        validarOwnership(idPessoa);

        Pessoa pessoa = pessoaGateway.buscarPorId(idPessoa).orElseThrow(() -> RecursoNaoEncontradoException.builder()
                .mensagem("Usuário não encontrado: " + idPessoa).detalhes("").build());

        Servico servico = servicoGateway.buscarPorId(idServico).orElseThrow(() -> RecursoNaoEncontradoException
                .builder().mensagem("Serviço não encontrado: " + idServico).detalhes("").build());

        if (favoritoGateway.existePorPessoaEServico(idPessoa, idServico)) {
            throw RecursoJaExisteException.builder().mensagem("Esse serviço já está nos favoritos deste usuário.")
                    .detalhes("").build();
        }

        favoritoGateway.salvar(Favorito.criar(pessoa, servico));
    }

    private void validarOwnership(Long idPessoa) {
        Long usuarioId = usuarioAutenticadoGateway.obterIdUsuarioAutenticado();
        if (!usuarioId.equals(idPessoa)) {
            throw AcessoNegadoException.builder().mensagem("você não tem permissão para acessar este recurso")
                    .detalhes("tentativa de operação nos favoritos de outro usuário").build();
        }
    }
}
