package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.repository.favorito.FavoritoRepository;
import com.automotiva.estetica.rick.infrastructure.entity.FavoritoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.FavoritoEntityMapper;
import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.domain.gateway.FavoritoGateway;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoritoGatewayImpl implements FavoritoGateway {

    private final FavoritoRepository favoritoRepository;
    private final FavoritoEntityMapper favoritoEntityMapper;

    @Override
    public Favorito salvar(Favorito favorito) {
        FavoritoEntity entity = favoritoEntityMapper.toEntity(favorito);
        return favoritoEntityMapper.toDomain(favoritoRepository.save(entity));
    }

    @Override
    public Optional<Favorito> buscarPorId(Long id) {
        return favoritoRepository.findById(id).map(favoritoEntityMapper::toDomain);
    }

    @Override
    public List<Favorito> buscarPorPessoaId(Long pessoaId) {
        return favoritoRepository.findByPessoaId(pessoaId).stream().map(favoritoEntityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorPessoaEServico(Long pessoaId, Long servicoId) {
        return favoritoRepository.existsByPessoaIdAndServicoId(pessoaId, servicoId);
    }

    @Override
    public void deletarPorId(Long id) {
        favoritoRepository.deleteById(id);
    }
}
