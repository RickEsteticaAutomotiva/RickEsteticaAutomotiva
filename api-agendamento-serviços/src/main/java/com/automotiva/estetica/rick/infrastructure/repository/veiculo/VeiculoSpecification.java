package com.automotiva.estetica.rick.infrastructure.repository.veiculo;

import com.automotiva.estetica.rick.infrastructure.entity.VeiculoEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class VeiculoSpecification {

    private VeiculoSpecification() {
    }

    public static Specification<VeiculoEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank())
                return cb.conjunction();
            if (query != null)
                query.distinct(true);
            String like = "%" + filtro.toLowerCase() + "%";
            var joinPessoa = root.join("pessoa", JoinType.LEFT);
            return cb.or(cb.like(cb.lower(root.get("placa")), like), cb.like(cb.lower(root.get("modelo")), like),
                    cb.like(cb.lower(root.get("marca")), like), cb.like(cb.lower(joinPessoa.get("nome")), like));
        };
    }
}
