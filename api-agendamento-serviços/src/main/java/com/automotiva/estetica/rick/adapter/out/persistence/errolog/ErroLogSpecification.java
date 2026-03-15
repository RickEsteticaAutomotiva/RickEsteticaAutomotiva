package com.automotiva.estetica.rick.adapter.out.persistence.errolog;

import com.automotiva.estetica.rick.adapter.out.persistence.jpaentity.ErroLogJpaEntity;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications JPA para filtros dinâmicos na consulta de ErroLog.
 *
 * <p>
 * Camada: adapter/out/persistence — infraestrutura.
 */
public final class ErroLogSpecification {

    private ErroLogSpecification() {
    }

    public static Specification<ErroLogJpaEntity> comFiltros(String tipoExcecao, Integer statusHttp,
            String usuarioEmail, LocalDateTime de, LocalDateTime ate) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipoExcecao != null && !tipoExcecao.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("tipoExcecao")), "%" + tipoExcecao.toLowerCase() + "%"));
            }
            if (statusHttp != null) {
                predicates.add(cb.equal(root.get("statusHttp"), statusHttp));
            }
            if (usuarioEmail != null && !usuarioEmail.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("usuarioEmail")), "%" + usuarioEmail.toLowerCase() + "%"));
            }
            if (de != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), de));
            }
            if (ate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), ate));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("timestamp")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
