package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.infrastructure.entity.ErroLogEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.ErroLogEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.errolog.ErroLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de ErroLogGatewayImpl")
class ErroLogGatewayImplTest {

    @Mock
    private ErroLogRepository erroLogRepository;

    @Mock
    private ErroLogEntityMapper erroLogEntityMapper;

    @InjectMocks
    private ErroLogGatewayImpl gateway;

    @Test
    @SuppressWarnings("unchecked")
    void salvarBuscarPorIdBuscarTodosEBuscarComFiltros_deveMapearDelegar() {
        ErroLog domain = ErroLog.builder().id(1L).build();
        ErroLogEntity entity = ErroLogEntity.builder().id(1L).build();
        Page<ErroLogEntity> pageEntity = new PageImpl<>(List.of(entity));

        when(erroLogEntityMapper.toEntity(domain)).thenReturn(entity);
        when(erroLogRepository.save(entity)).thenReturn(entity);
        when(erroLogEntityMapper.toDomain(entity)).thenReturn(domain);
        when(erroLogRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(erroLogRepository.findAll(PageRequest.of(0, 10))).thenReturn(pageEntity);
        when(erroLogRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class),
                any(org.springframework.data.domain.Pageable.class))).thenReturn(pageEntity);

        ErroLog salvo = gateway.salvar(domain);
        Optional<ErroLog> porId = gateway.buscarPorId(1L);
        Page<ErroLog> todos = gateway.buscarTodos(PageRequest.of(0, 10));
        Page<ErroLog> filtrados = gateway.buscarComFiltros("RuntimeException", 500, "admin@test.com",
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(1L, salvo.getId());
        assertTrue(porId.isPresent());
        assertEquals(1, todos.getTotalElements());
        assertEquals(1, filtrados.getTotalElements());
    }

    @Test
    void deletarAnterioresA_deveDelegarRepositorio() {
        LocalDateTime limite = LocalDateTime.of(2026, 4, 3, 0, 0);

        gateway.deletarAnterioresA(limite);

        verify(erroLogRepository).deleteByTimestampBefore(limite);
    }
}


