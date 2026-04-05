package com.automotiva.estetica.rick.domain.gateway;

import com.automotiva.estetica.rick.domain.entity.ErroLog;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ErroLogGateway {

    ErroLog salvar(ErroLog erroLog);

    Optional<ErroLog> buscarPorId(Long id);

    Page<ErroLog> buscarTodos(Pageable pageable);

    Page<ErroLog> buscarComFiltros(String tipoExcecao, Integer statusHttp, String usuarioEmail, LocalDateTime de,
            LocalDateTime ate, Pageable pageable);

    void deletarAnterioresA(LocalDateTime dataLimite);
}
