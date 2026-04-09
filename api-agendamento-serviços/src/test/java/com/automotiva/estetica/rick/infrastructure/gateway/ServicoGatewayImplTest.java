package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.ServicoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.servico.ServicoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ServicoGatewayImplTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoEntityMapper servicoEntityMapper;

    private ServicoGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        gateway = new ServicoGatewayImpl(servicoRepository, servicoEntityMapper);
    }

    @Test
    void salvar_deveMapearEDelegar() {
        Servico servico = Servico.builder().id(1L).nome("Lavagem").build();
        ServicoEntity entity = ServicoEntity.builder().id(1L).nome("Lavagem").build();

        when(servicoEntityMapper.toEntity(servico)).thenReturn(entity);
        when(servicoRepository.save(entity)).thenReturn(entity);
        when(servicoEntityMapper.toDomain(entity)).thenReturn(servico);

        Servico resultado = gateway.salvar(servico);

        assertEquals(1L, resultado.getId());
        verify(servicoRepository).save(entity);
    }

    @Test
    void buscarPorIdEBuscarTodos_devemMapear() {
        ServicoEntity entity = ServicoEntity.builder().id(2L).nome("Polimento").build();
        Servico servico = Servico.builder().id(2L).nome("Polimento").build();

        when(servicoRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(servicoEntityMapper.toDomain(entity)).thenReturn(servico);
        when(servicoRepository.findAll(ArgumentMatchers.<Specification<ServicoEntity>>any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(java.util.List.of(entity)));

        Optional<Servico> byId = gateway.buscarPorId(2L);
        var page = gateway.buscarTodos("pol", PageRequest.of(0, 10));

        assertTrue(byId.isPresent());
        assertEquals("Polimento", byId.orElseThrow().getNome());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void buscarPorIds_deveMapearLista() {
        ServicoEntity entity = ServicoEntity.builder().id(3L).nome("Higienizacao").build();
        Servico servico = Servico.builder().id(3L).nome("Higienizacao").build();

        when(servicoRepository.findByIdIn(List.of(3L))).thenReturn(List.of(entity));
        when(servicoEntityMapper.toDomain(entity)).thenReturn(servico);

        var resultado = gateway.buscarPorIds(List.of(3L));

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.getFirst().getId());
    }

    @Test
    void existePorId_deveDelegarRepositorio() {
        when(servicoRepository.existsById(8L)).thenReturn(true);

        assertTrue(gateway.existePorId(8L));
    }

    @Test
    void deletarPorId_quandoExiste_deveAplicarSoftDelete() {
        ServicoEntity entity = ServicoEntity.builder().id(9L).nome("Vitrificacao").build();
        when(servicoRepository.findById(9L)).thenReturn(Optional.of(entity));

        gateway.deletarPorId(9L);

        ArgumentCaptor<ServicoEntity> captor = ArgumentCaptor.forClass(ServicoEntity.class);
        verify(servicoRepository).save(captor.capture());
        assertNotNull(captor.getValue().getDeletadoEm());
    }

    @Test
    void deletarPorId_quandoNaoExiste_deveLancarRecursoNaoEncontrado() {
        when(servicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> gateway.deletarPorId(99L));
    }
}
