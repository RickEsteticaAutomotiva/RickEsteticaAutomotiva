package com.automotiva.estetica.rick.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.CategoriaJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.jpa.ServicoJpaEntity;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.CategoriaPersistenceMapper;
import com.automotiva.estetica.rick.adapter.out.persistence.mapper.ServicoPersistenceMapper;
import com.automotiva.estetica.rick.domain.entity.Servico;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({ServicoRepositoryAdapter.class, ServicoPersistenceMapper.class, CategoriaPersistenceMapper.class})
@DisplayName("Persistência — ServicoRepositoryAdapter")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ServicoRepositoryAdapterIT {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ServicoRepositoryAdapter repositoryAdapter;

    private CategoriaJpaEntity categoria;

    @BeforeEach
    void setUp() {
        categoria = em.persistFlushFind(
                CategoriaJpaEntity.builder().nome("Lavagem IT").build());
    }

    private ServicoJpaEntity persistirServico(String nome, BigDecimal preco) {
        return em.persistFlushFind(ServicoJpaEntity.builder()
                .nome(nome)
                .descricao("Descrição " + nome)
                .preco(preco)
                .categoria(categoria)
                .build());
    }

    @Test
    @DisplayName("salvar → persiste novo serviço e retorna domínio com ID")
    void salvar_sucesso() {
        Servico servico = Servico.builder()
                .nome("Polimento IT")
                .descricao("Polimento de teste")
                .preco(BigDecimal.valueOf(99.90))
                .categoria(com.automotiva.estetica.rick.domain.entity.Categoria.builder()
                        .id(categoria.getId())
                        .build())
                .build();

        Servico salvo = repositoryAdapter.salvar(servico);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Polimento IT");
        assertThat(salvo.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(99.90));
    }

    @Test
    @DisplayName("buscarPorId → retorna domínio quando ID existe")
    void buscarPorId_encontrado() {
        ServicoJpaEntity jpa = persistirServico("Lavagem Encontrada", BigDecimal.valueOf(30));

        Optional<Servico> resultado = repositoryAdapter.buscarPorId(jpa.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Lavagem Encontrada");
    }

    @Test
    @DisplayName("buscarPorId → retorna Optional vazio quando ID não existe")
    void buscarPorId_naoEncontrado() {
        assertThat(repositoryAdapter.buscarPorId(99999L)).isEmpty();
    }

    @Test
    @DisplayName("buscarTodos → filtra por nome via ServicoSpecification")
    void buscarTodos_filtroNome() {
        persistirServico("Enceramento Premium", BigDecimal.valueOf(80));
        persistirServico("Detalhamento Básico", BigDecimal.valueOf(150));

        Page<Servico> resultado = repositoryAdapter.buscarTodos("Enceramento", PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty().allSatisfy(s -> assertThat(s.getNome())
                .containsIgnoringCase("Enceramento"));
    }

    @Test
    @DisplayName("buscarTodos → retorna todos quando filtro é nulo")
    void buscarTodos_semFiltro() {
        persistirServico("Servico Sem Filtro", BigDecimal.valueOf(50));

        Page<Servico> resultado = repositoryAdapter.buscarTodos(null, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("buscarPorIds → retorna lista de serviços pelos IDs informados")
    void buscarPorIds_sucesso() {
        ServicoJpaEntity s1 = persistirServico("Serviço A", BigDecimal.valueOf(10));
        ServicoJpaEntity s2 = persistirServico("Serviço B", BigDecimal.valueOf(20));

        List<Servico> resultado = repositoryAdapter.buscarPorIds(List.of(s1.getId(), s2.getId()));

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Servico::getNome).containsExactlyInAnyOrder("Serviço A", "Serviço B");
    }

    @Test
    @DisplayName("existePorId → retorna true quando serviço existe")
    void existePorId_existente() {
        ServicoJpaEntity jpa = persistirServico("Existe ID", BigDecimal.valueOf(60));

        assertThat(repositoryAdapter.existePorId(jpa.getId())).isTrue();
    }

    @Test
    @DisplayName("existePorId → retorna false quando serviço não existe")
    void existePorId_naoExistente() {
        assertThat(repositoryAdapter.existePorId(99999L)).isFalse();
    }

    @Test
    @DisplayName("deletarPorId → remove serviço do banco")
    void deletarPorId_sucesso() {
        ServicoJpaEntity jpa = persistirServico("Deletar IT", BigDecimal.valueOf(40));

        repositoryAdapter.deletarPorId(jpa.getId());
        em.flush();

        assertThat(em.find(ServicoJpaEntity.class, jpa.getId())).isNull();
    }
}
