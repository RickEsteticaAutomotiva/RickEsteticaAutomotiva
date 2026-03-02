package com.automotiva.estetica.rick.application.port.out;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Porta de saída — contrato de persistência do log de erros. */
public interface ErroLogRepositoryPort {

    ErroLog salvar(ErroLog erroLog);

    Optional<ErroLog> buscarPorId(Long id);

    Page<ErroLog> buscarTodos(Pageable pageable);

    Page<ErroLog> buscarComFiltros(
            String tipoExcecao,
            Integer statusHttp,
            String usuarioEmail,
            LocalDateTime de,
            LocalDateTime ate,
            Pageable pageable);

    void deletarAnterioresA(LocalDateTime dataLimite);
}
