package com.automotiva.estetica.rick.api_agendamento_servicos.repository;

import com.automotiva.estetica.rick.api_agendamento_servicos.dto.FaturamentoMensalQuery;
import com.automotiva.estetica.rick.api_agendamento_servicos.dto.RegistroFaturamentoQuery;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import com.automotiva.estetica.rick.api_agendamento_servicos.entity.OrdemServicoEntity;
import org.springframework.stereotype.Repository;
@Repository
public interface DashRepository extends JpaRepository<OrdemServicoEntity, Long> {

    @Query("""
        SELECT new com.automotiva.estetica.rick.api_agendamento_servicos.dto.RegistroFaturamentoQuery(
            c.nome,
            s.nome,
            SUM(i.preco),
            SUM(i.preco - os.precoMinimo)
        )
        FROM OrdemServicoEntity os
        JOIN ItemServicoEntity i ON i.ordemServico.id = os.id
        JOIN ServicoEntity s ON s.id = i.servico.id
        JOIN CategoriaEntity c ON c.id = s.categoria.id
        WHERE (:mes IS NULL OR MONTH(os.dataAgendamento) = :mes)
          AND (:ano IS NULL OR YEAR(os.dataAgendamento) = :ano)
        GROUP BY c.nome, s.nome
    """)
    List<RegistroFaturamentoQuery> buscarFaturamentoAgrupado(
            @Param("mes") Integer mes,
            @Param("ano") Integer ano
    );


    @Query("SELECT new com.automotiva.estetica.rick.api_agendamento_servicos.dto.FaturamentoMensalQuery(\n" +
            "    SUM(o.precoMinimo), SUM(o.precoMinimo))\n" +
            "FROM OrdemServicoEntity o\n" +
            "WHERE MONTH(o.dataAgendamento) = :mes AND YEAR(o.dataAgendamento) = :ano\n")
    List<FaturamentoMensalQuery> buscarFaturamentoPorMes(@Param("mes") int mes, @Param("ano") int ano);

}