package com.automotiva.estetica.rick.adapter.out.persistence;

import com.automotiva.estetica.rick.adapter.out.persistence.jpa.ServicoJpaEntity;
import org.springframework.data.jpa.domain.Specification;

public final class ServicoSpecification {

    private ServicoSpecification() {}

    public static Specification<ServicoJpaEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank()) return cb.conjunction();
            String like = "%" + filtro.toLowerCase() + "%";
            return cb.or(cb.like(cb.lower(root.get("nome")), like), cb.like(cb.lower(root.get("descricao")), like));
        };
    }
}
