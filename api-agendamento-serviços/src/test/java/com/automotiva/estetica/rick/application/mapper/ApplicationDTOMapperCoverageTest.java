package com.automotiva.estetica.rick.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.automotiva.estetica.rick.application.dto.request.CategoriaRequest;
import com.automotiva.estetica.rick.application.dto.request.PessoaCadastroRequest;
import com.automotiva.estetica.rick.application.dto.request.VeiculoRequest;
import com.automotiva.estetica.rick.application.dto.response.ErroLogResponse;
import com.automotiva.estetica.rick.application.dto.response.VeiculoResponse;
import com.automotiva.estetica.rick.domain.entity.Categoria;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.enums.RoleEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ApplicationDTOMapperCoverageTest {

    private final CategoriaDTOMapper categoriaMapper = Mappers.getMapper(CategoriaDTOMapper.class);
    private final PessoaDTOMapper pessoaMapper = Mappers.getMapper(PessoaDTOMapper.class);
    private final VeiculoDTOMapper veiculoMapper = Mappers.getMapper(VeiculoDTOMapper.class);
    private final ErroLogDTOMapper erroLogMapper = Mappers.getMapper(ErroLogDTOMapper.class);

    @Test
    void categoriaMapper_deveMapearDomainEResponse() {
        CategoriaRequest request = CategoriaRequest.builder().nome("Lavagem").build();
        Categoria categoria = categoriaMapper.toDomain(request);

        assertNotNull(categoria);
        assertEquals("Lavagem", categoria.getNome());

        var response = categoriaMapper.toResponse(Categoria.builder().id(9L).nome("Polimento").build());
        assertEquals(9L, response.getId());
        assertEquals("Polimento", response.getNome());
    }

    @Test
    void pessoaMapper_deveMapearCadastroEResposta() {
        PessoaCadastroRequest request = PessoaCadastroRequest.builder().nome("Ana")
                .cpf("12345678900").email("ana@x.com").telefone("11999999999")
                .dataNascimento(LocalDate.of(1995, 5, 10)).senha("hash").roles(EnumSet.of(RoleEnum.ROLE_GERENTE))
                .build();

        Pessoa domain = pessoaMapper.toDomain(request);
        assertEquals("Ana", domain.getNome());
        assertEquals("ana@x.com", domain.getEmail());
        assertEquals("hash", domain.getSenha());

        var response = pessoaMapper.toResponse(Pessoa.builder().id(5L).nome("Ana")
                .roles(EnumSet.of(RoleEnum.ROLE_GERENTE)).build());
        assertEquals(5L, response.getId());
        assertEquals("Ana", response.getNome());
        assertEquals(EnumSet.of(RoleEnum.ROLE_GERENTE), response.getRoles());
    }

    @Test
    void veiculoMapper_toDomainEToResponse_comPessoaPreenchida() {
        VeiculoRequest request = VeiculoRequest.builder().idPessoa(12L).placa("ABC1D23").modelo("A3").build();

        Veiculo domain = veiculoMapper.toDomain(request);
        assertEquals(12L, domain.getPessoa().getId());
        assertEquals("ABC1D23", domain.getPlaca());

        VeiculoResponse response = veiculoMapper.toResponse(Veiculo.builder().id(22L).placa("ABC1D23")
                .pessoa(Pessoa.builder().id(12L).build()).build());
        assertEquals(22L, response.getId());
        assertEquals(12L, response.getIdPessoa());
    }

    @Test
    void veiculoMapper_toResponse_quandoPessoaNula_deveManterIdPessoaNulo() {
        VeiculoResponse response = veiculoMapper
                .toResponse(Veiculo.builder().id(23L).placa("XYZ9Z99").pessoa(null).build());

        assertEquals(23L, response.getId());
        assertNull(response.getIdPessoa());
    }

    @Test
    void erroLogMapper_deveMapearCamposPrincipais() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 4, 4, 12, 30);
        ErroLog erroLog = ErroLog.builder().id(7L).timestamp(timestamp).tipoExcecao("IllegalStateException")
                .mensagem("falha").endpoint("/api/ordens").metodoHttp("POST").statusHttp(500).build();

        ErroLogResponse response = erroLogMapper.toResponse(erroLog);

        assertEquals(7L, response.getId());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals("IllegalStateException", response.getTipoExcecao());
        assertEquals("falha", response.getMensagem());
        assertEquals("/api/ordens", response.getEndpoint());
        assertEquals("POST", response.getMetodoHttp());
        assertEquals(500, response.getStatusHttp());
    }
}

