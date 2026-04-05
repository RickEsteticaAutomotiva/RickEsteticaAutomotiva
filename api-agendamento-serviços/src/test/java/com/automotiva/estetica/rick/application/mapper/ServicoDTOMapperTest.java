package com.automotiva.estetica.rick.application.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.automotiva.estetica.rick.application.dto.request.ServicoRequest;
import com.automotiva.estetica.rick.application.dto.response.ServicoResponse;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.entity.Servico;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ServicoDTOMapperTest {

    private final ServicoDTOMapper mapper = Mappers.getMapper(ServicoDTOMapper.class);

    @Test
    void toDomain_deveMapearCategoriaIdParaCategoriaInterna() {
        ServicoRequest request = ServicoRequest.builder().nome("Polimento").descricao("Descricao")
                .preco(BigDecimal.valueOf(99.9)).imagem("img").duracaoHoras(LocalTime.of(1, 30)).categoriaId(7L)
                .build();

        Servico resultado = mapper.toDomain(request);

        assertNotNull(resultado);
        assertNotNull(resultado.getCategoria());
        assertEquals(7L, resultado.getCategoria().getId());
        assertEquals("Polimento", resultado.getNome());
    }

    @Test
    void toResponse_deveMapearCategoriaParaCamposDaResposta() {
        Servico servico = Servico.builder().id(11L).nome("Higienizacao")
                .categoria(Categoria.builder().id(2L).nome("Interna").build()).build();

        ServicoResponse response = mapper.toResponse(servico);

        assertNotNull(response);
        assertEquals(2L, response.getCategoriaId());
        assertEquals("Interna", response.getCategoriaNome());
        assertEquals(11L, response.getId());
    }

    @Test
    void toResponse_quandoCategoriaNula_deveManterCamposNulos() {
        Servico servico = Servico.builder().id(12L).nome("Lavagem").categoria(null).build();

        ServicoResponse response = mapper.toResponse(servico);

        assertNotNull(response);
        assertNull(response.getCategoriaId());
        assertNull(response.getCategoriaNome());
    }
}

