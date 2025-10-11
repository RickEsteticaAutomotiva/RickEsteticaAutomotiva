package com.automotiva.estetica.rick.api_agendamento_servicos.specification;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.PessoaEntity;
import org.springframework.data.jpa.domain.Specification;

public class PessoaSpecification {

    public static Specification<PessoaEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isEmpty()) {
                return cb.conjunction();
            }
            String likeFiltro = "%" + filtro.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("nome")), likeFiltro),
                    cb.like(cb.lower(root.get("cpf")), likeFiltro),
                    cb.like(cb.lower(root.get("email")), likeFiltro),
                    cb.like(
                            cb.lower(
                                    cb.function("TO_CHAR", String.class, root.get("dataNascimento"), cb.literal("yyyy-MM-dd"))
                            ),
                            likeFiltro
                    )
            );
        };
    }
}