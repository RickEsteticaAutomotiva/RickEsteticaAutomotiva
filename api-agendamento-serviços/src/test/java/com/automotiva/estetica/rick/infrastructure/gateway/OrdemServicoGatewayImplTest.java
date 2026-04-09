package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import com.automotiva.estetica.rick.infrastructure.repository.ordemservico.OrdemServicoDuracaoProjection;
import com.automotiva.estetica.rick.infrastructure.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.OrdemServicoEntityMapper;
import com.automotiva.estetica.rick.infrastructure.repository.ordemservico.OrdemServicoRepository;
import java.time.LocalDate;
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
@DisplayName("Testes de OrdemServicoGatewayImpl")
class OrdemServicoGatewayImplTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoEntityMapper ordemServicoEntityMapper;

    @InjectMocks
    private OrdemServicoGatewayImpl gateway;

    @Test
    @SuppressWarnings("unchecked")
    void salvarEBuscas_deveMapearDelegar() {
        OrdemServico domain = OrdemServico.builder().id(1L).build();
        OrdemServicoEntity entity = OrdemServicoEntity.builder().id(1L).build();
        Page<OrdemServicoEntity> pageEntity = new PageImpl<>(List.of(entity));

        when(ordemServicoEntityMapper.toEntity(domain)).thenReturn(entity);
        when(ordemServicoRepository.save(entity)).thenReturn(entity);
        when(ordemServicoEntityMapper.toDomain(entity)).thenReturn(domain);
        when(ordemServicoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(ordemServicoRepository.findByVeiculo_Pessoa_Id(9L)).thenReturn(List.of(entity));
        when(ordemServicoRepository.findOrdemServicoById(1L)).thenReturn(Optional.of(entity));
        when(ordemServicoRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class),
                any(org.springframework.data.domain.Pageable.class))).thenReturn(pageEntity);

        OrdemServico salvo = gateway.salvar(domain);
        Optional<OrdemServico> porId = gateway.buscarPorId(1L);
        Page<OrdemServico> todos = gateway.buscarTodos("filtro", PageRequest.of(0, 10));
        List<OrdemServico> porPessoa = gateway.buscarPorVeiculoPessoaId(9L);
        Optional<OrdemServico> detalhe = gateway.buscarPorIdComDetalhes(1L);
        Page<OrdemServico> gestao = gateway.buscarTodosParaGestao(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(1L, salvo.getId());
        assertTrue(porId.isPresent());
        assertEquals(1, todos.getTotalElements());
        assertEquals(1, porPessoa.size());
        assertTrue(detalhe.isPresent());
        assertEquals(1, gestao.getTotalElements());
    }

    @Test
    void existePorVeiculoIdEDataAgendamento_deveDelegarRepositorio() {
        LocalDateTime data = LocalDateTime.of(2026, 4, 3, 10, 0);
        when(ordemServicoRepository.existsByVeiculoIdAndDataAgendamento(3L, data)).thenReturn(true);

        boolean existe = gateway.existePorVeiculoIdEDataAgendamento(3L, data);

        assertTrue(existe);
    }

    @Test
    void buscarDuracaoTotalPorOS_deveMapearProjecao() {
        LocalDate data = LocalDate.of(2025, 12, 1);
        OrdemServicoDuracaoProjection projection = new OrdemServicoDuracaoProjection() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public LocalDateTime getDataAgendamento() {
                return LocalDateTime.of(2025, 12, 1, 10, 0);
            }

            @Override
            public Long getDuracaoTotal() {
                return 180L;
            }
        };

        when(ordemServicoRepository.buscarDuracaoTotalPorOS(data)).thenReturn(List.of(projection));

        var resultado = gateway.buscarDuracaoTotalPorOS(data);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.getFirst().id());
        assertEquals(180L, resultado.getFirst().duracaoTotal());
    }
}
