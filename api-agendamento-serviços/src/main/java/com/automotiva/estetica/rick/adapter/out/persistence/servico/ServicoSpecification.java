package com.automotiva.estetica.rick.adapter.out.persistence.servico;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ServicoJpaEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class ServicoSpecification {

    private ServicoSpecification() {
    }

    public static Specification<ServicoJpaEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank())
                return cb.conjunction();
            if (query != null)
                query.distinct(true);
            String like = "%" + filtro.toLowerCase() + "%";
            var joinCategoria = root.join("categoria", JoinType.LEFT);
            return cb.or(cb.like(cb.lower(root.get("nome")), like), cb.like(cb.lower(root.get("descricao")), like),
                    cb.like(cb.lower(joinCategoria.get("nome")), like));
        };
    }
}
