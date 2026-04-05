package com.automotiva.estetica.rick.infrastructure.repository.pessoa;

import com.automotiva.estetica.rick.infrastructure.entity.PessoaEntity;
import org.springframework.data.jpa.domain.Specification;

public final class PessoaSpecification {

    private PessoaSpecification() {
    }

    public static Specification<PessoaEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank())
                return cb.conjunction();
            String like = "%" + filtro.toLowerCase() + "%";
            return cb.or(cb.like(cb.lower(root.get("nome")), like), cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("cpf")), like));
        };
    }
}
