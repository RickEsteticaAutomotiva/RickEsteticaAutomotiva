package com.automotiva.estetica.rick.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.automotiva.estetica.rick.application.dto.request.OrdemServicoGestaoPageRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("Testes de PageableFactory")
class PageableFactoryTest {

    @Test
    @DisplayName("from(PageRequest) deve aplicar ordenacao padrao por id")
    void fromPageRequest_deveAplicarOrdenacaoPadrao() {
        PageRequest pageRequest = PageRequest.builder().pagina(1).tamanho(5).ordenarPor(" ").build();

        Pageable pageable = PageableFactory.from(pageRequest);

        assertEquals(1, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("id").getDirection());
    }

    @Test
    @DisplayName("from(PageRequest) deve aplicar multiplos campos com trim")
    void fromPageRequest_deveAplicarMultiplosCamposComTrim() {
        PageRequest pageRequest = PageRequest.builder().pagina(0).tamanho(10).ordenarPor("nome, preco").build();

        Pageable pageable = PageableFactory.from(pageRequest);

        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("nome").getDirection());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("preco").getDirection());
    }

    @Test
    @DisplayName("from(OrdemServicoGestaoPageRequest) deve aplicar defaults de ordenacao e direcao")
    void fromGestaoRequest_deveAplicarDefaults() {
        OrdemServicoGestaoPageRequest pageRequest = OrdemServicoGestaoPageRequest.builder().pagina(2).tamanho(15)
                .ordenarPor(null).direcao(" ").build();

        Pageable pageable = PageableFactory.from(pageRequest);

        assertEquals(2, pageable.getPageNumber());
        assertEquals(15, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("dataAgendamento").getDirection());
    }

    @Test
    @DisplayName("from(OrdemServicoGestaoPageRequest) deve respeitar direcao asc")
    void fromGestaoRequest_deveRespeitarDirecaoAsc() {
        OrdemServicoGestaoPageRequest pageRequest = OrdemServicoGestaoPageRequest.builder().pagina(0).tamanho(20)
                .ordenarPor("status,dataAgendamento").direcao("asc").build();

        Pageable pageable = PageableFactory.from(pageRequest);

        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("status").getDirection());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("dataAgendamento").getDirection());
    }
}
