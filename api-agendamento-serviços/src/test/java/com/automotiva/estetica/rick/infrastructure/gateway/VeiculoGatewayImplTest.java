package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.exception.RecursoNaoEncontradoException;
import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.VeiculoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.veiculo.VeiculoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de VeiculoGatewayImpl")
class VeiculoGatewayImplTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoEntityMapper veiculoEntityMapper;

    @InjectMocks
    private VeiculoGatewayImpl gateway;

    @Test
    void salvarBuscarPorIdBuscarTodosBuscarPorPessoaIdEExistePorId_deveMapearDelegar() {
        Veiculo domain = Veiculo.builder().id(1L).build();
        VeiculoEntity entity = VeiculoEntity.builder().id(1L).build();

        when(veiculoEntityMapper.toEntity(domain)).thenReturn(entity);
        when(veiculoRepository.save(entity)).thenReturn(entity);
        when(veiculoEntityMapper.toDomain(entity)).thenReturn(domain);
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(veiculoRepository.findAll()).thenReturn(List.of(entity));
        when(veiculoRepository.findByPessoa_Id(3L)).thenReturn(List.of(entity));
        when(veiculoRepository.existsById(1L)).thenReturn(true);

        Veiculo salvo = gateway.salvar(domain);
        Optional<Veiculo> porId = gateway.buscarPorId(1L);
        List<Veiculo> todos = gateway.buscarTodos();
        List<Veiculo> porPessoa = gateway.buscarPorPessoaId(3L);
        boolean existe = gateway.existePorId(1L);

        assertEquals(1L, salvo.getId());
        assertTrue(porId.isPresent());
        assertEquals(1, todos.size());
        assertEquals(1, porPessoa.size());
        assertTrue(existe);
    }

    @Test
    void deletarPorId_quandoExiste_deveAplicarSoftDelete() {
        VeiculoEntity entity = VeiculoEntity.builder().id(9L).build();
        when(veiculoRepository.findById(9L)).thenReturn(Optional.of(entity));

        gateway.deletarPorId(9L);

        ArgumentCaptor<VeiculoEntity> captor = ArgumentCaptor.forClass(VeiculoEntity.class);
        verify(veiculoRepository).save(captor.capture());
        assertNotNull(captor.getValue().getDeletadoEm());
    }

    @Test
    void deletarPorId_quandoNaoExiste_deveLancarRecursoNaoEncontrado() {
        when(veiculoRepository.findById(88L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> gateway.deletarPorId(88L));
    }
}

