package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.automotiva.estetica.rick.infrastructure.entity.CategoriaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.CategoriaEntityMapperImpl;
import com.automotiva.estetica.rick.infrastructure.mapper.ServicoEntityMapperImpl;
import com.automotiva.estetica.rick.domain.entity.Servico;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
@Import({ServicoGatewayImpl.class, ServicoEntityMapperImpl.class, CategoriaEntityMapperImpl.class})
@DisplayName("Persistencia - ServicoGatewayImpl")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class ServicoGatewayImplIT {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private EntityManager rawEm;

    @Autowired
    private ServicoGatewayImpl gateway;

    private CategoriaEntity categoria;

    @BeforeEach
    void setUp() {
        categoria = em.persistFlushFind(CategoriaEntity.builder().nome("Lavagem IT").build());
    }

    private ServicoEntity persistirServico(String nome, BigDecimal preco) {
        return em.persistFlushFind(ServicoEntity.builder().nome(nome).descricao("Descricao " + nome).preco(preco)
                .duracaoHoras(LocalTime.of(1, 0)).categoria(categoria).build());
    }

    @Test
    @DisplayName("salvar - persiste novo servico e retorna dominio com ID")
    void salvar_sucesso() {
        Servico servico = Servico.builder().nome("Polimento IT").descricao("Polimento de teste")
                .preco(BigDecimal.valueOf(99.90)).duracaoHoras(LocalTime.of(2, 0))
                .categoria(com.automotiva.estetica.rick.domain.entity.Categoria.builder().id(categoria.getId()).build())
                .build();

        Servico salvo = gateway.salvar(servico);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Polimento IT");
        assertThat(salvo.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(99.90));
    }

    @Test
    @DisplayName("buscarPorId - retorna dominio quando ID existe")
    void buscarPorId_encontrado() {
        ServicoEntity jpa = persistirServico("Lavagem Encontrada", BigDecimal.valueOf(30));

        Optional<Servico> resultado = gateway.buscarPorId(jpa.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Lavagem Encontrada");
    }

    @Test
    @DisplayName("buscarPorId - retorna Optional vazio quando ID nao existe")
    void buscarPorId_naoEncontrado() {
        assertThat(gateway.buscarPorId(99999L)).isEmpty();
    }

    @Test
    @DisplayName("buscarTodos - filtra por nome via ServicoSpecification")
    void buscarTodos_filtroNome() {
        persistirServico("Enceramento Premium", BigDecimal.valueOf(80));
        persistirServico("Detalhamento Basico", BigDecimal.valueOf(150));

        Page<Servico> resultado = gateway.buscarTodos("Enceramento", PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty()
                .allSatisfy(s -> assertThat(s.getNome()).containsIgnoringCase("Enceramento"));
    }

    @Test
    @DisplayName("buscarTodos - retorna todos quando filtro e nulo")
    void buscarTodos_semFiltro() {
        persistirServico("Servico Sem Filtro", BigDecimal.valueOf(50));

        Page<Servico> resultado = gateway.buscarTodos(null, PageRequest.of(0, 10));

        assertThat(resultado.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("existePorId - retorna true quando servico existe")
    void existePorId_existente() {
        ServicoEntity jpa = persistirServico("Existe ID", BigDecimal.valueOf(60));

        assertThat(gateway.existePorId(jpa.getId())).isTrue();
    }

    @Test
    @DisplayName("existePorId - retorna false quando servico nao existe")
    void existePorId_naoExistente() {
        assertThat(gateway.existePorId(99999L)).isFalse();
    }

    @Test
    @DisplayName("buscarTodos - filtra por nome da categoria via ServicoSpecification")
    void buscarTodos_filtroNomeCategoria() {
        CategoriaEntity outraCategoria = em
                .persistFlushFind(CategoriaEntity.builder().nome("Vitrificacao IT").build());

        em.persistFlushFind(ServicoEntity.builder().nome("Servico de Vidro").descricao("Descricao generica")
                .preco(BigDecimal.valueOf(200)).categoria(outraCategoria).build());

        persistirServico("Polimento Basico", BigDecimal.valueOf(100));

        Page<Servico> resultado = gateway.buscarTodos("Vitrificacao", PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().getFirst().getNome()).isEqualTo("Servico de Vidro");
    }

    @Test
    @DisplayName("deletarPorId - aplica soft-delete preenchendo deletadoEm e oculta da busca")
    void deletarPorId_sucesso() {
        ServicoEntity jpa = persistirServico("Deletar IT", BigDecimal.valueOf(40));
        Long id = jpa.getId();

        gateway.deletarPorId(id);
        em.flush();
        em.clear();

        Object resultado = rawEm.createNativeQuery("SELECT deletado_em FROM servico WHERE id = :id")
                .setParameter("id", id).getSingleResult();

        LocalDateTime deletadoEm = resultado instanceof java.sql.Timestamp ts
                ? ts.toLocalDateTime()
                : (LocalDateTime) resultado;

        assertThat(deletadoEm).isNotNull();
        assertThat(gateway.buscarPorId(id)).isEmpty();
        assertThat(gateway.existePorId(id)).isFalse();
    }
}
