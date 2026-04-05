package com.automotiva.estetica.rick.infrastructure.repository.errolog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.automotiva.estetica.rick.infrastructure.entity.ErroLogEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ErroLogSpecificationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ErroLogRepository repository;

    @BeforeEach
    void setUp() {
        em.persistFlushFind(ErroLogEntity.builder().timestamp(LocalDateTime.of(2026, 4, 2, 10, 0))
                .tipoExcecao("java.lang.IllegalArgumentException").mensagem("Parametro invalido")
                .endpoint("/api/servicos").metodoHttp("GET").usuarioEmail("joao@test.com").statusHttp(400)
                .ambiente("test").ipCliente("127.0.0.1").build());

        em.persistFlushFind(ErroLogEntity.builder().timestamp(LocalDateTime.of(2026, 4, 2, 11, 0))
                .tipoExcecao("java.lang.RuntimeException").mensagem("Falha inesperada").endpoint("/api/pessoas")
                .metodoHttp("POST").usuarioEmail("maria@test.com").statusHttp(500).ambiente("test")
                .ipCliente("127.0.0.2").build());

        em.persistFlushFind(ErroLogEntity.builder().timestamp(LocalDateTime.of(2026, 4, 2, 9, 30))
                .tipoExcecao("org.springframework.security.AccessDeniedException").mensagem("Acesso negado")
                .endpoint("/api/admin").metodoHttp("GET").usuarioEmail("admin@test.com").statusHttp(403)
                .ambiente("test").ipCliente("127.0.0.3").build());
    }

    @Test
    void comFiltros_quandoSemFiltros_deveRetornarTodosOrdenadosPorTimestampDesc() {
        List<ErroLogEntity> resultado = repository.findAll(ErroLogSpecification.comFiltros(null, null, null, null, null));

        assertEquals(3, resultado.size());
        assertEquals(LocalDateTime.of(2026, 4, 2, 11, 0), resultado.get(0).getTimestamp());
        assertEquals(LocalDateTime.of(2026, 4, 2, 10, 0), resultado.get(1).getTimestamp());
        assertEquals(LocalDateTime.of(2026, 4, 2, 9, 30), resultado.get(2).getTimestamp());
    }

    @Test
    void comFiltros_deveFiltrarPorTipoExcecaoCaseInsensitive() {
        long total = repository.findAll(ErroLogSpecification.comFiltros("runtimeexception", null, null, null, null)).size();

        assertEquals(1, total);
    }

    @Test
    void comFiltros_deveFiltrarPorStatusHttp() {
        long total = repository.findAll(ErroLogSpecification.comFiltros(null, 403, null, null, null)).size();

        assertEquals(1, total);
    }

    @Test
    void comFiltros_deveIgnorarUsuarioEmailBlank() {
        long total = repository.findAll(ErroLogSpecification.comFiltros(null, null, "   ", null, null)).size();

        assertEquals(3, total);
    }

    @Test
    void comFiltros_deveAplicarIntervaloDeDatas() {
        long total = repository.findAll(ErroLogSpecification.comFiltros(null, null, null,
                LocalDateTime.of(2026, 4, 2, 9, 45), LocalDateTime.of(2026, 4, 2, 10, 30))).size();

        assertEquals(1, total);
    }
}

