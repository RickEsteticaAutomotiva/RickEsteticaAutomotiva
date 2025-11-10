package com.automotiva.estetica.rick.api_agendamento_servicos.specification;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.ServicoEntity;
import org.springframework.data.jpa.domain.Specification;

public class ServicoSpecification {

    public static Specification<ServicoEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank()) {
                return cb.conjunction();
            }

            String likeFiltro = "%" + filtro.toLowerCase() + "%";

            var categoriaJoin = root.join("categoria");

            return cb.or(
                    cb.like(cb.lower(root.get("nome")), likeFiltro),
                    cb.like(cb.lower(root.get("descricao")), likeFiltro),
                    cb.like(cb.lower(cb.function("TO_CHAR", String.class, root.get("preco"), cb.literal("999999.99"))), likeFiltro),
                    cb.like(cb.lower(categoriaJoin.get("nome")), likeFiltro)
            );
        };
    }
}
