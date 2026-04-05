package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.repository.categoria.CategoriaRepository;
import com.automotiva.estetica.rick.infrastructure.mapper.CategoriaEntityMapper;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.gateway.CategoriaGateway;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ImplementaÃ§Ã£o do Gateway de Categoria. Atua como adaptador de saÃ­da entre
 * domÃ­nio e persistÃªncia JPA.
 *
 * <p>
 * Essa camada garante que o domÃ­nio dependa apenas da abstraÃ§Ã£o
 * {@link CategoriaGateway}, desacoplando-o de detalhes tÃ©cnicos de
 * persistÃªncia.
 *
 * <p>
 * Camada: infrastructure/gateway.
 */
@Component
@RequiredArgsConstructor
public class CategoriaGatewayImpl implements CategoriaGateway {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaEntityMapper categoriaEntityMapper;

    @Override
    public Categoria salvar(Categoria categoria) {
        return categoriaEntityMapper
                .toDomain(categoriaRepository.save(categoriaEntityMapper.toEntity(categoria)));
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id).map(categoriaEntityMapper::toDomain);
    }

    @Override
    public List<Categoria> buscarTodas() {
        return categoriaRepository.findAll().stream().map(categoriaEntityMapper::toDomain).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return categoriaRepository.existsById(id);
    }

    @Override
    public void deletarPorId(Long id) {
        categoriaRepository.deleteById(id);
    }
}
