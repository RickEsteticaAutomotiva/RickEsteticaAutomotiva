package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.exception.AcessoNegadoException;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.domain.gateway.FavoritoGateway;
import com.automotiva.estetica.rick.domain.gateway.UsuarioAutenticadoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoverFavoritoUseCase {

    private final FavoritoGateway favoritoGateway;
    private final UsuarioAutenticadoGateway usuarioAutenticadoGateway;

    public void execute(Long idFavorito) {
        Favorito favorito = favoritoGateway.buscarPorId(idFavorito).orElseThrow(() -> RecursoNaoEncontradoException
                .builder().mensagem("Favorito não encontrado.").detalhes("").build());

        validarOwnership(favorito.getPessoa().getId());
        favoritoGateway.deletarPorId(idFavorito);
    }

    private void validarOwnership(Long idPessoaDona) {
        Long usuarioId = usuarioAutenticadoGateway.obterIdUsuarioAutenticado();
        if (!usuarioId.equals(idPessoaDona)) {
            throw AcessoNegadoException.builder().mensagem("você não tem permissão para acessar este recurso")
                    .detalhes("tentativa de operação nos favoritos de outro usuário").build();
        }
    }
}
