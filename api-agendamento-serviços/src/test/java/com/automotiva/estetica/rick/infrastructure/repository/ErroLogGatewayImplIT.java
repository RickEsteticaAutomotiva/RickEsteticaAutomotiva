package com.automotiva.estetica.rick.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.automotiva.estetica.rick.infrastructure.gateway.ErroLogGatewayImpl;
import com.automotiva.estetica.rick.infrastructure.mapper.ErroLogEntityMapperImpl;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import java.time.LocalDateTime;
import java.util.Optional;
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
@Import({ErroLogGatewayImpl.class, ErroLogEntityMapperImpl.class})
@DisplayName("Persistencia - ErroLogGatewayImpl")
class ErroLogGatewayImplIT {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ErroLogGatewayImpl erroLogGateway;

    private ErroLog criarErroLog(LocalDateTime timestamp, String tipoExcecao, Integer statusHttp, String usuarioEmail) {
        return ErroLog.builder().timestamp(timestamp).tipoExcecao(tipoExcecao).mensagem("Falha ao processar requisicao")
                .stackTrace("stack trace de teste").endpoint("/api/teste").metodoHttp("GET").usuarioEmail(usuarioEmail)
                .statusHttp(statusHttp).ambiente("test").ipCliente("127.0.0.1").userAgent("JUnit").build();
    }

    @Test
    @DisplayName("salvar - persiste erro e retorna dominio com ID")
    void salvar_sucesso() {
        ErroLog erro = criarErroLog(LocalDateTime.now(), "java.lang.IllegalArgumentException", 400, "teste@email.com");

        ErroLog salvo = erroLogGateway.salvar(erro);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getTipoExcecao()).isEqualTo("java.lang.IllegalArgumentException");
        assertThat(salvo.getStatusHttp()).isEqualTo(400);
    }

    @Test
    @DisplayName("buscarPorId - retorna Optional com dominio quando ID existe")
    void buscarPorId_encontrado() {
        ErroLog salvo = erroLogGateway
                .salvar(criarErroLog(LocalDateTime.now(), "java.lang.RuntimeException", 500, "id@email.com"));

        Optional<ErroLog> resultado = erroLogGateway.buscarPorId(salvo.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(salvo.getId());
    }

    @Test
    @DisplayName("buscarPorId - retorna Optional vazio quando ID nao existe")
    void buscarPorId_naoEncontrado() {
        assertThat(erroLogGateway.buscarPorId(999999L)).isEmpty();
    }

    @Test
    @DisplayName("buscarTodos(Pageable) - retorna pagina com itens")
    void buscarTodos_paginado() {
        erroLogGateway.salvar(criarErroLog(LocalDateTime.now().minusMinutes(2), "ExA", 400, "a@email.com"));
        erroLogGateway.salvar(criarErroLog(LocalDateTime.now().minusMinutes(1), "ExB", 500, "b@email.com"));

        Page<ErroLog> pagina = erroLogGateway.buscarTodos(PageRequest.of(0, 10));

        assertThat(pagina.getContent()).isNotEmpty();
        assertThat(pagina.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("buscarComFiltros - aplica filtros dinamicos e ordena por timestamp desc")
    void buscarComFiltros_sucesso() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 10, 10, 0);

        erroLogGateway.salvar(criarErroLog(base.minusDays(1), "IllegalStateException", 500, "alvo@email.com"));
        erroLogGateway.salvar(criarErroLog(base.plusMinutes(5), "IllegalStateException", 500, "alvo@email.com"));
        erroLogGateway.salvar(criarErroLog(base.plusMinutes(10), "NullPointerException", 500, "alvo@email.com"));

        Page<ErroLog> resultado = erroLogGateway.buscarComFiltros("illegalstate", 500, "ALVO@EMAIL.COM", base,
                base.plusHours(1), PageRequest.of(0, 10));

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().getFirst().getTimestamp()).isEqualTo(base.plusMinutes(5));
    }

    @Test
    @DisplayName("deletarAnterioresA - remove somente registros antes da data limite")
    void deletarAnterioresA_sucesso() {
        LocalDateTime limite = LocalDateTime.of(2026, 2, 1, 0, 0);

        ErroLog antigo = erroLogGateway.salvar(criarErroLog(limite.minusDays(2), "OldException", 500, "old@email.com"));
        ErroLog recente = erroLogGateway.salvar(criarErroLog(limite.plusDays(1), "NewException", 500, "new@email.com"));

        erroLogGateway.deletarAnterioresA(limite);
        em.flush();
        em.clear();

        assertThat(erroLogGateway.buscarPorId(antigo.getId())).isEmpty();
        assertThat(erroLogGateway.buscarPorId(recente.getId())).isPresent();
    }
}
