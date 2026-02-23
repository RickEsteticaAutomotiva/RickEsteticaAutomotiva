package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.OrdemServicoJpaEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class OrdemServicoSpecification {

    private OrdemServicoSpecification() {}

    public static Specification<OrdemServicoJpaEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank()) return cb.conjunction();
            String like = "%" + filtro.toLowerCase() + "%";
            var joinVeiculo = root.join("veiculo", JoinType.LEFT);
            var joinStatus = root.join("status", JoinType.LEFT);
            var joinMotivo = root.join("motivoCancelamento", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("observacoes")), like),
                    cb.like(cb.lower(joinVeiculo.get("placa")), like),
                    cb.like(cb.lower(joinVeiculo.get("modelo")), like),
                    cb.like(cb.lower(joinStatus.get("descricao")), like),
                    cb.like(cb.lower(joinMotivo.get("descricao")), like));
        };
    }
}
