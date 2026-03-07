package com.automotiva.estetica.rick.adapter.out.persistence.errolog;

import com.automotiva.estetica.rick.adapter.out.persistence.mapper.ErroLogPersistenceMapper;
import com.automotiva.estetica.rick.application.port.out.ErroLogRepositoryPort;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Implementação da porta de saída — persiste ErroLog via JPA.
 *
 * <p>Camada: adapter/out/persistence.
 */
@Repository
@RequiredArgsConstructor
public class ErroLogRepositoryAdapter implements ErroLogRepositoryPort {

    private final ErroLogJpaRepository jpaRepository;
    private final ErroLogPersistenceMapper mapper;

    @Override
    public ErroLog salvar(ErroLog erroLog) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(erroLog)));
    }

    @Override
    public Optional<ErroLog> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<ErroLog> buscarTodos(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<ErroLog> buscarComFiltros(
            String tipoExcecao,
            Integer statusHttp,
            String usuarioEmail,
            LocalDateTime de,
            LocalDateTime ate,
            Pageable pageable) {

        return jpaRepository
                .findAll(
                        ErroLogSpecification.comFiltros(
                                tipoExcecao, statusHttp, usuarioEmail, de, ate),
                        pageable)
                .map(mapper::toDomain);
    }

    @Override
    public void deletarAnterioresA(LocalDateTime dataLimite) {
        jpaRepository.deleteByTimestampBefore(dataLimite);
    }
}
