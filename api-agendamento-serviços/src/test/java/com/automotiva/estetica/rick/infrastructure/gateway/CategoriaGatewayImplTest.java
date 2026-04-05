package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.infrastructure.entity.CategoriaEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.CategoriaEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.categoria.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoriaGatewayImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaEntityMapper categoriaEntityMapper;

    private CategoriaGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        gateway = new CategoriaGatewayImpl(categoriaRepository, categoriaEntityMapper);
    }

    @Test
    void salvar_deveMapearEDelegar() {
        Categoria categoria = Categoria.builder().id(1L).nome("Lavagem").build();
        CategoriaEntity entity = CategoriaEntity.builder().id(1L).nome("Lavagem").build();

        when(categoriaEntityMapper.toEntity(categoria)).thenReturn(entity);
        when(categoriaRepository.save(entity)).thenReturn(entity);
        when(categoriaEntityMapper.toDomain(entity)).thenReturn(categoria);

        Categoria resultado = gateway.salvar(categoria);

        assertEquals(1L, resultado.getId());
        verify(categoriaRepository).save(entity);
    }

    @Test
    void buscarPorId_deveRetornarOptionalMapeado() {
        CategoriaEntity entity = CategoriaEntity.builder().id(2L).nome("Polimento").build();
        Categoria categoria = Categoria.builder().id(2L).nome("Polimento").build();

        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(categoriaEntityMapper.toDomain(entity)).thenReturn(categoria);

        Optional<Categoria> resultado = gateway.buscarPorId(2L);

        assertTrue(resultado.isPresent());
        assertEquals("Polimento", resultado.orElseThrow().getNome());
    }

    @Test
    void buscarTodas_deveMapearLista() {
        CategoriaEntity a = CategoriaEntity.builder().id(10L).nome("A").build();
        CategoriaEntity b = CategoriaEntity.builder().id(11L).nome("B").build();

        when(categoriaRepository.findAll()).thenReturn(List.of(a, b));
        when(categoriaEntityMapper.toDomain(a)).thenReturn(Categoria.builder().id(10L).nome("A").build());
        when(categoriaEntityMapper.toDomain(b)).thenReturn(Categoria.builder().id(11L).nome("B").build());

        List<Categoria> resultado = gateway.buscarTodas();

        assertEquals(2, resultado.size());
        assertEquals("A", resultado.getFirst().getNome());
    }

    @Test
    void existePorIdEDeletarPorId_devemDelegarRepositorio() {
        when(categoriaRepository.existsById(7L)).thenReturn(true);

        assertTrue(gateway.existePorId(7L));
        gateway.deletarPorId(7L);

        verify(categoriaRepository).deleteById(7L);
    }
}

