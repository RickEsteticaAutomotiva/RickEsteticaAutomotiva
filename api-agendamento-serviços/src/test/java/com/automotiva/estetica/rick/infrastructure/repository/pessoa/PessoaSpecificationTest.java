package com.automotiva.estetica.rick.infrastructure.repository.pessoa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PessoaSpecificationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PessoaRepository repository;

    @BeforeEach
    void setUp() {
        em.persistFlushFind(PessoaEntity.builder().nome("Joao Silva").cpf("11122233344").email("joao@test.com")
                .telefone("11999990001").dataNascimento(LocalDate.of(1990, 1, 10)).senha("123").build());

        em.persistFlushFind(PessoaEntity.builder().nome("Maria Souza").cpf("55566677788").email("maria@test.com")
                .telefone("11999990002").dataNascimento(LocalDate.of(1992, 2, 20)).senha("456").build());
    }

    @Test
    void filtroUnico_quandoNulo_deveRetornarTodos() {
        long total = repository.findAll(PessoaSpecification.filtroUnico(null)).size();

        assertEquals(2L, total);
    }

    @Test
    void filtroUnico_quandoBlank_deveRetornarTodos() {
        long total = repository.findAll(PessoaSpecification.filtroUnico("   ")).size();

        assertEquals(2L, total);
    }

    @Test
    void filtroUnico_deveFiltrarPorNomeEmailOuCpf() {
        assertEquals(1, repository.findAll(PessoaSpecification.filtroUnico("joao")).size());
        assertEquals(1, repository.findAll(PessoaSpecification.filtroUnico("maria@test.com")).size());
        assertEquals(1, repository.findAll(PessoaSpecification.filtroUnico("55566677788")).size());
    }
}

