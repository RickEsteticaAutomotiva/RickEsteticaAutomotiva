package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.ItemServico;
import com.automotiva.estetica.rick.infrastructure.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.ItemServicoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.itemservico.ItemServicoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ItemServicoGatewayImpl")
class ItemServicoGatewayImplTest {

    @Mock
    private ItemServicoRepository itemServicoRepository;

    @Mock
    private ItemServicoEntityMapper itemServicoEntityMapper;

    @InjectMocks
    private ItemServicoGatewayImpl gateway;

    @Test
    void salvarEBuscas_deveMapearDelegar() {
        ItemServico domain = ItemServico.builder().id(1L).build();
        ItemServicoEntity entity = ItemServicoEntity.builder().id(1L).build();

        when(itemServicoEntityMapper.toEntity(domain)).thenReturn(entity);
        when(itemServicoRepository.save(entity)).thenReturn(entity);
        when(itemServicoEntityMapper.toDomain(entity)).thenReturn(domain);
        when(itemServicoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(itemServicoRepository.findAll()).thenReturn(List.of(entity));
        when(itemServicoRepository.findByOrdemServico_Id(9L)).thenReturn(List.of(entity));
        when(itemServicoRepository.findByOrdemServico_IdAndServico_Id(9L, 3L)).thenReturn(Optional.of(entity));
        when(itemServicoRepository.existsByOrdemServico_IdAndServico_Id(9L, 3L)).thenReturn(true);

        ItemServico salvo = gateway.salvar(domain);
        Optional<ItemServico> porId = gateway.buscarPorId(1L);
        List<ItemServico> todos = gateway.buscarTodos();
        List<ItemServico> porOrdem = gateway.buscarPorOrdemServicoId(9L);
        Optional<ItemServico> porPar = gateway.buscarPorOrdemServicoIdEServicoId(9L, 3L);
        boolean existe = gateway.existePorOrdemServicoIdEServicoId(9L, 3L);

        assertEquals(1L, salvo.getId());
        assertTrue(porId.isPresent());
        assertEquals(1, todos.size());
        assertEquals(1, porOrdem.size());
        assertTrue(porPar.isPresent());
        assertTrue(existe);
    }

    @Test
    void removerPorId_deveDelegarRepositorio() {
        gateway.removerPorId(77L);

        verify(itemServicoRepository).deleteById(77L);
    }
}
