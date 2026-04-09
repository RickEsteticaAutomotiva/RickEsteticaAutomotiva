package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Favorito;
import com.automotiva.estetica.rick.infrastructure.entity.FavoritoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.FavoritoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.favorito.FavoritoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de FavoritoGatewayImpl")
class FavoritoGatewayImplTest {

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private FavoritoEntityMapper favoritoEntityMapper;

    @InjectMocks
    private FavoritoGatewayImpl gateway;

    @Test
    void salvar_deveMapearSalvarERetornarDominio() {
        Favorito domain = Favorito.builder().id(1L).build();
        FavoritoEntity entity = FavoritoEntity.builder().id(1L).build();

        when(favoritoEntityMapper.toEntity(domain)).thenReturn(entity);
        when(favoritoRepository.save(entity)).thenReturn(entity);
        when(favoritoEntityMapper.toDomain(entity)).thenReturn(domain);

        Favorito resultado = gateway.salvar(domain);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_deveRetornarOptionalMapeado() {
        FavoritoEntity entity = FavoritoEntity.builder().id(10L).build();
        Favorito domain = Favorito.builder().id(10L).build();
        when(favoritoRepository.findById(10L)).thenReturn(Optional.of(entity));
        when(favoritoEntityMapper.toDomain(entity)).thenReturn(domain);

        Optional<Favorito> resultado = gateway.buscarPorId(10L);

        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.orElseThrow().getId());
    }

    @Test
    void buscarPorPessoaId_existePorPessoaEServico_eDeletarPorId_deveDelegarRepositorio() {
        FavoritoEntity entity = FavoritoEntity.builder().id(20L).build();
        Favorito domain = Favorito.builder().id(20L).build();
        when(favoritoRepository.findByPessoaId(7L)).thenReturn(List.of(entity));
        when(favoritoEntityMapper.toDomain(entity)).thenReturn(domain);
        when(favoritoRepository.existsByPessoaIdAndServicoId(7L, 3L)).thenReturn(true);

        List<Favorito> resultado = gateway.buscarPorPessoaId(7L);
        boolean existe = gateway.existePorPessoaEServico(7L, 3L);
        gateway.deletarPorId(20L);

        assertEquals(1, resultado.size());
        assertTrue(existe);
        verify(favoritoRepository).deleteById(20L);
    }
}
