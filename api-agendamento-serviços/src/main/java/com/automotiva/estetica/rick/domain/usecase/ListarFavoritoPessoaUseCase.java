package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.gateway.FavoritoGateway;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarFavoritoPessoaUseCase {

    private final FavoritoGateway favoritoGateway;
    private final PessoaGateway pessoaGateway;
    private final UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    public List<Favorito> execute(Long idPessoa) {
        validarOwnership(idPessoa);

        if (!pessoaGateway.existePorId(idPessoa)) {
            return List.of();
        }

        return favoritoGateway.buscarPorPessoaId(idPessoa);
    }

    private void validarOwnership(Long idPessoa) {
        Long usuarioId = usuarioAutenticadoGateway.obterIdUsuarioAutenticado();
        if (!usuarioId.equals(idPessoa)) {
            throw AcessoNegadoException.builder().mensagem("você não tem permissão para acessar este recurso")
                    .detalhes("tentativa de operação nos favoritos de outro usuário").build();
        }
    }
}
