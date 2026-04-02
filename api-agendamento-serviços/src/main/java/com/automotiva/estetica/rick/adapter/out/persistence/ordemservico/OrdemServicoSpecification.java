package com.automotiva.estetica.rick.adapter.out.persistence.ordemservico;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.OrdemServicoJpaEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class OrdemServicoSpecification {

    private OrdemServicoSpecification() {
    }

    public static Specification<OrdemServicoJpaEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank())
                return cb.conjunction();
            String like = "%" + filtro.toLowerCase() + "%";
            var joinVeiculo = root.join("veiculo", JoinType.LEFT);
            var joinStatus = root.join("status", JoinType.LEFT);
            var joinMotivo = root.join("motivoCancelamento", JoinType.LEFT);
            return cb.or(cb.like(cb.lower(root.get("observacoes")), like),
                    cb.like(cb.lower(joinVeiculo.get("placa")), like),
                    cb.like(cb.lower(joinVeiculo.get("modelo")), like),
                    cb.like(cb.lower(joinStatus.get("descricao")), like),
                    cb.like(cb.lower(joinMotivo.get("descricao")), like));
        };
    }

    public static Specification<OrdemServicoJpaEntity> filtroGestao(
            String filtro, Long status, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status").get("id"), status));
            }
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataAgendamento"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataAgendamento"), dataFim));
            }

            if (filtro != null && !filtro.isBlank()) {
                String like = "%" + filtro.toLowerCase() + "%";
                var joinVeiculo = root.join("veiculo", JoinType.LEFT);
                var joinStatus = root.join("status", JoinType.LEFT);
                var joinMotivo = root.join("motivoCancelamento", JoinType.LEFT);
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("observacoes")), like),
                                cb.like(cb.lower(joinVeiculo.get("placa")), like),
                                cb.like(cb.lower(joinVeiculo.get("modelo")), like),
                                cb.like(cb.lower(joinStatus.get("descricao")), like),
                                cb.like(cb.lower(joinMotivo.get("descricao")), like)));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
