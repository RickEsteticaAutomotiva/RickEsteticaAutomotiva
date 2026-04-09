package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Carrinho;
import com.automotiva.estetica.rick.infrastructure.entity.CarrinhoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.CarrinhoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.carrinho.CarrinhoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CarrinhoGatewayImpl")
class CarrinhoGatewayImplTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private CarrinhoEntityMapper carrinhoEntityMapper;

    @InjectMocks
    private CarrinhoGatewayImpl gateway;

    @Test
    void salvarEbuscarPorId_deveMapearCorretamente() {
        Carrinho domain = Carrinho.builder().id(1L).build();
        CarrinhoEntity entity = CarrinhoEntity.builder().id(1L).build();

        when(carrinhoEntityMapper.toEntity(domain)).thenReturn(entity);
        when(carrinhoRepository.save(entity)).thenReturn(entity);
        when(carrinhoEntityMapper.toDomain(entity)).thenReturn(domain);
        when(carrinhoRepository.findById(1L)).thenReturn(Optional.of(entity));

        Carrinho salvo = gateway.salvar(domain);
        Optional<Carrinho> buscado = gateway.buscarPorId(1L);

        assertEquals(1L, salvo.getId());
        assertTrue(buscado.isPresent());
    }

    @Test
    void buscarPorPessoaIdEExistePorPessoaEServico_deveDelegarRepositorio() {
        CarrinhoEntity entity = CarrinhoEntity.builder().id(2L).build();
        Carrinho domain = Carrinho.builder().id(2L).build();
        when(carrinhoRepository.findByPessoaId(8L)).thenReturn(List.of(entity));
        when(carrinhoEntityMapper.toDomain(entity)).thenReturn(domain);
        when(carrinhoRepository.existsByPessoaIdAndServicoId(8L, 9L)).thenReturn(true);

        List<Carrinho> itens = gateway.buscarPorPessoaId(8L);
        boolean existe = gateway.existePorPessoaEServico(8L, 9L);

        assertEquals(1, itens.size());
        assertTrue(existe);
    }

    @Test
    void deletarPorIdEdeletarTodos_deveDelegarRepositorio() {
        Carrinho itemA = Carrinho.builder().id(10L).build();
        Carrinho itemB = Carrinho.builder().id(11L).build();
        CarrinhoEntity entityA = CarrinhoEntity.builder().id(10L).build();
        CarrinhoEntity entityB = CarrinhoEntity.builder().id(11L).build();

        when(carrinhoEntityMapper.toEntity(itemA)).thenReturn(entityA);
        when(carrinhoEntityMapper.toEntity(itemB)).thenReturn(entityB);

        gateway.deletarPorId(10L);
        gateway.deletarTodos(List.of(itemA, itemB));

        verify(carrinhoRepository).deleteById(10L);
        verify(carrinhoRepository).deleteAll(List.of(entityA, entityB));
    }
}
