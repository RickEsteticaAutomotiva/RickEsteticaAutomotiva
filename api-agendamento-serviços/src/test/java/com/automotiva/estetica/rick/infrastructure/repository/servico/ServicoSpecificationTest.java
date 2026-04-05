package com.automotiva.estetica.rick.infrastructure.repository.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.automotiva.estetica.rick.infrastructure.entity.CategoriaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ServicoSpecificationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ServicoRepository repository;

    @BeforeEach
    void setUp() {
        CategoriaEntity categoriaLavagem = em.persistFlushFind(CategoriaEntity.builder().nome("Lavagem").build());
        CategoriaEntity categoriaEstetica = em.persistFlushFind(CategoriaEntity.builder().nome("Estetica").build());

        em.persistFlushFind(ServicoEntity.builder().nome("Lavagem Premium").descricao("Lavagem completa")
                .preco(BigDecimal.valueOf(100)).categoria(categoriaLavagem).build());

        em.persistFlushFind(ServicoEntity.builder().nome("Higienizacao interna").descricao("Detalhamento interno")
                .preco(BigDecimal.valueOf(180)).categoria(categoriaEstetica).build());
    }

    @Test
    void filtroUnico_quandoNulo_deveRetornarTodos() {
        long total = repository.findAll(ServicoSpecification.filtroUnico(null)).size();

        assertEquals(2L, total);
    }

    @Test
    void filtroUnico_quandoBlank_deveRetornarTodos() {
        long total = repository.findAll(ServicoSpecification.filtroUnico("   ")).size();

        assertEquals(2L, total);
    }

    @Test
    void filtroUnico_deveFiltrarPorNomeDescricaoOuCategoria() {
        assertEquals(1, repository.findAll(ServicoSpecification.filtroUnico("premium")).size());
        assertEquals(1, repository.findAll(ServicoSpecification.filtroUnico("detalhamento")).size());
        assertEquals(1, repository.findAll(ServicoSpecification.filtroUnico("estetica")).size());
    }
}

