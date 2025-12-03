package com.automotiva.estetica.rick.api_agendamento_servicos.specification;

import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class OrdemServicoSpecification {

    public static Specification<OrdemServicoEntity> filtroUnico(String filtro) {
        return (root, query, cb) -> {
            if (filtro == null || filtro.isBlank()) {
                return cb.conjunction();
            }

            String likeFiltro = "%" + filtro.toLowerCase() + "%";

            var joinVeiculo = root.join("veiculo", JoinType.LEFT);
            var joinStatus = root.join("status", JoinType.LEFT);
            var joinMotivo = root.join("motivoCancelamento", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("observacoes")), likeFiltro),
                    cb.like(
                            cb.lower(cb.function(
                                    "TO_CHAR",
                                    String.class,
                                    root.get("dataAgendamento"),
                                    cb.literal("yyyy-MM-dd HH24:MI"))),
                            likeFiltro),
                    cb.like(
                            cb.lower(cb.function(
                                    "TO_CHAR",
                                    String.class,
                                    root.get("dtConclusao"),
                                    cb.literal("yyyy-MM-dd HH24:MI"))),
                            likeFiltro),
                    cb.like(cb.lower(joinVeiculo.get("placa")), likeFiltro),
                    cb.like(cb.lower(joinVeiculo.get("modelo")), likeFiltro),
                    cb.like(cb.lower(joinStatus.get("descricao")), likeFiltro),
                    cb.like(cb.lower(joinMotivo.get("descricao")), likeFiltro));
        };
    }
}
