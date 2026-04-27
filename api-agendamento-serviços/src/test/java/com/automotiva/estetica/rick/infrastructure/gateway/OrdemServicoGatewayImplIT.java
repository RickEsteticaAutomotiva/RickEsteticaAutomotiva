package com.automotiva.estetica.rick.infrastructure.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.automotiva.estetica.rick.infrastructure.entity.OrdemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.CategoriaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.ItemServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import com.automotiva.estetica.rick.infrastructure.entity.ServicoEntity;
import com.automotiva.estetica.rick.infrastructure.entity.StatusEntity;
import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import com.automotiva.estetica.rick.infrastructure.mapper.OrdemServicoEntityMapperImpl;
import com.automotiva.estetica.rick.infrastructure.mapper.PessoaEntityMapperImpl;
import com.automotiva.estetica.rick.infrastructure.mapper.VeiculoEntityMapperImpl;
import com.automotiva.estetica.rick.domain.entity.OrdemServico;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({OrdemServicoGatewayImpl.class, OrdemServicoEntityMapperImpl.class, VeiculoEntityMapperImpl.class,
        PessoaEntityMapperImpl.class})
@DisplayName("Persistencia - OrdemServicoGatewayImpl")
class OrdemServicoGatewayImplIT {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrdemServicoGatewayImpl gateway;

    private VeiculoEntity veiculo;
    private StatusEntity statusAnalise;
    private StatusEntity statusConfirmada;

    @BeforeEach
    void setUp() {
        PessoaEntity pessoa = em.persistFlushFind(
                PessoaEntity.builder().nome("Pessoa OS").cpf("12312312312").email("pessoa.os@email.com")
                        .telefone("11999990000").dataNascimento(LocalDate.of(1990, 1, 1)).senha("$2a$10$hash").build());

        veiculo = em.persistFlushFind(VeiculoEntity.builder().placa("TST0001").modelo("Celta").marca("GM")
                .porte("Pequeno").cor("Prata").ano("2010").pessoa(pessoa).build());

        statusAnalise = em.persistFlushFind(StatusEntity.builder().descricao("ANALISE").build());
        statusConfirmada = em.persistFlushFind(StatusEntity.builder().descricao("AGENDA CONFIRMADA").build());
        em.persistFlushFind(StatusEntity.builder().descricao("CONCLUIDO").build());
        em.persistFlushFind(StatusEntity.builder().descricao("CANCELADO").build());
    }

    private OrdemServicoEntity persistirOrdem(LocalDateTime data, StatusEntity status, BigDecimal preco) {
        return em.persistFlushFind(OrdemServicoEntity.builder().dataAgendamento(data).precoMinimo(preco)
                .veiculo(veiculo).status(status).observacoes("Teste").build());
    }

    @Test
    @DisplayName("buscarPorId - retorna dominio quando ordem existe")
    void buscarPorId_encontrado() {
        OrdemServicoEntity jpa = persistirOrdem(LocalDateTime.now().plusDays(1), statusAnalise,
                BigDecimal.valueOf(100));

        Optional<OrdemServico> resultado = gateway.buscarPorId(jpa.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(jpa.getId());
        assertThat(resultado.get().getPrecoMinimo()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("buscarPorId - retorna Optional vazio quando ID inexistente")
    void buscarPorId_naoEncontrado() {
        assertThat(gateway.buscarPorId(99999L)).isEmpty();
    }

    @Test
    @DisplayName("existePorVeiculoIdEDataAgendamento - true para conflito de agendamento")
    void existeConflito_verdadeiro() {
        LocalDateTime data = LocalDateTime.of(2026, 5, 10, 14, 0);
        persistirOrdem(data, statusAnalise, BigDecimal.valueOf(50));

        assertThat(gateway.existePorVeiculoIdEDataAgendamento(veiculo.getId(), data)).isTrue();
    }

    @Test
    @DisplayName("existePorVeiculoIdEDataAgendamento - false quando nao ha conflito")
    void existeConflito_falso() {
        assertThat(gateway.existePorVeiculoIdEDataAgendamento(veiculo.getId(), LocalDateTime.of(2030, 1, 1, 8, 0)))
                .isFalse();
    }

    @Test
    @DisplayName("buscarPorVeiculoPessoaId - retorna ordens vinculadas a pessoa")
    void buscarPorPessoa_sucesso() {
        persistirOrdem(LocalDateTime.now().plusDays(5), statusAnalise, BigDecimal.valueOf(200));

        var ordens = gateway.buscarPorVeiculoPessoaId(veiculo.getPessoa().getId());

        assertThat(ordens).isNotEmpty();
        assertThat(ordens)
                .allSatisfy(o -> assertThat(o.getVeiculo().getPessoa().getId()).isEqualTo(veiculo.getPessoa().getId()));
    }

    @Test
    @DisplayName("buscarPorVeiculoPessoaId - lista vazia quando pessoa nao tem ordens")
    void buscarPorPessoa_vazio() {
        var ordens = gateway.buscarPorVeiculoPessoaId(99999L);

        assertThat(ordens).isEmpty();
    }

    @Test
    @DisplayName("buscarDuracaoTotalPorOS - retorna duracao total por ordem no dia")
    void buscarDuracaoTotalPorOS_sucesso() {
        CategoriaEntity categoria = em.persistFlushFind(CategoriaEntity.builder().nome("Lavagem").build());
        ServicoEntity servico1 = em.persistFlushFind(ServicoEntity.builder().nome("Servico 1").preco(BigDecimal.TEN)
                .duracaoMinutos(60).categoria(categoria).build());
        ServicoEntity servico2 = em.persistFlushFind(ServicoEntity.builder().nome("Servico 2").preco(BigDecimal.ONE)
                .duracaoMinutos(30).categoria(categoria).build());

        LocalDateTime dataAgendamento = LocalDateTime.of(2026, 5, 10, 14, 0);
        OrdemServicoEntity ordem = persistirOrdem(dataAgendamento, statusConfirmada, BigDecimal.valueOf(50));

        em.persistFlushFind(
                ItemServicoEntity.builder().ordemServico(ordem).servico(servico1).preco(BigDecimal.TEN).build());
        em.persistFlushFind(
                ItemServicoEntity.builder().ordemServico(ordem).servico(servico2).preco(BigDecimal.ONE).build());

        var resultado = gateway.buscarDuracaoTotalPorOS(LocalDate.of(2026, 5, 10));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().id()).isEqualTo(ordem.getId());
        assertThat(resultado.getFirst().duracaoTotal()).isEqualTo(30L);
    }
}
