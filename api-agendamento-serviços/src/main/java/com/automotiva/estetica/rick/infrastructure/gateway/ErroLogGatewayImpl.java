package com.automotiva.estetica.rick.infrastructure.gateway;

import com.automotiva.estetica.rick.infrastructure.repository.errolog.ErroLogRepository;
import com.automotiva.estetica.rick.infrastructure.repository.errolog.ErroLogSpecification;
import com.automotiva.estetica.rick.infrastructure.mapper.ErroLogEntityMapper;
import com.automotiva.estetica.rick.domain.entity.ErroLog;
import com.automotiva.estetica.rick.domain.gateway.ErroLogGateway;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErroLogGatewayImpl implements ErroLogGateway {

    private final ErroLogRepository erroLogRepository;
    private final ErroLogEntityMapper erroLogEntityMapper;

    @Override
    public ErroLog salvar(ErroLog erroLog) {
        return erroLogEntityMapper
                .toDomain(erroLogRepository.save(erroLogEntityMapper.toEntity(erroLog)));
    }

    @Override
    public Optional<ErroLog> buscarPorId(Long id) {
        return erroLogRepository.findById(id).map(erroLogEntityMapper::toDomain);
    }

    @Override
    public Page<ErroLog> buscarTodos(Pageable pageable) {
        return erroLogRepository.findAll(pageable).map(erroLogEntityMapper::toDomain);
    }

    @Override
    public Page<ErroLog> buscarComFiltros(String tipoExcecao, Integer statusHttp, String usuarioEmail, LocalDateTime de,
            LocalDateTime ate, Pageable pageable) {
        return erroLogRepository
                .findAll(ErroLogSpecification.comFiltros(tipoExcecao, statusHttp, usuarioEmail, de, ate), pageable)
                .map(erroLogEntityMapper::toDomain);
    }

    @Override
    public void deletarAnterioresA(LocalDateTime dataLimite) {
        erroLogRepository.deleteByTimestampBefore(dataLimite);
    }
}
