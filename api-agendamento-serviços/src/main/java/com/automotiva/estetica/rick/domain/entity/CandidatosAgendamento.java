package com.automotiva.estetica.rick.domain.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Candidatos reais (já cadastrados no sistema) encontrados a partir do
 * matching de um {@link DadosAgendamentoExtraido}. Listas vazias indicam que
 * nenhum registro real correspondeu — o gerente precisa buscar manualmente;
 * nunca é criado um registro novo automaticamente a partir daqui.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidatosAgendamento {

    private List<Pessoa> pessoas;
    private List<Veiculo> veiculos;
    private List<Servico> servicos;
}
