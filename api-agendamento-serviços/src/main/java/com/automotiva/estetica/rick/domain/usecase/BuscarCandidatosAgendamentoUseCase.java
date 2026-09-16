package com.automotiva.estetica.rick.domain.usecase;

import com.automotiva.estetica.rick.domain.entity.CandidatosAgendamento;
import com.automotiva.estetica.rick.domain.entity.DadosAgendamentoExtraido;
import com.automotiva.estetica.rick.domain.entity.Pessoa;
import com.automotiva.estetica.rick.domain.entity.Servico;
import com.automotiva.estetica.rick.domain.entity.Veiculo;
import com.automotiva.estetica.rick.domain.gateway.PessoaGateway;
import com.automotiva.estetica.rick.domain.gateway.ServicoGateway;
import com.automotiva.estetica.rick.domain.gateway.VeiculoGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * A partir dos dados extraídos de uma imagem, busca candidatos reais já
 * cadastrados no sistema (cliente, veículo, serviço) para o gerente
 * confirmar/escolher na tela de revisão. Nunca cria nada — apenas busca.
 */
@Service
@RequiredArgsConstructor
public class BuscarCandidatosAgendamentoUseCase {

    private static final int LIMITE_CANDIDATOS = 10;

    private final PessoaGateway pessoaGateway;
    private final VeiculoGateway veiculoGateway;
    private final ServicoGateway servicoGateway;

    public CandidatosAgendamento execute(DadosAgendamentoExtraido dados) {
        List<Pessoa> pessoas = dados.getNomeCliente() != null && dados.getNomeCliente().getValor() != null
                ? pessoaGateway.buscarTodos(dados.getNomeCliente().getValor(), PageRequest.of(0, LIMITE_CANDIDATOS))
                        .getContent()
                : List.of();

        String termoVeiculo = termoBuscaVeiculo(dados);
        List<Veiculo> veiculos = termoVeiculo != null ? veiculoGateway.buscarPorTermo(termoVeiculo) : List.of();

        List<Servico> servicos = dados.getDescricaoServico() != null && dados.getDescricaoServico().getValor() != null
                ? servicoGateway
                        .buscarTodos(dados.getDescricaoServico().getValor(), PageRequest.of(0, LIMITE_CANDIDATOS))
                        .getContent()
                : List.of();

        return CandidatosAgendamento.builder().pessoas(pessoas).veiculos(veiculos).servicos(servicos).build();
    }

    /**
     * Placa é o identificador mais confiável de veículo; na ausência dela, cai
     * para o modelo.
     */
    private String termoBuscaVeiculo(DadosAgendamentoExtraido dados) {
        if (dados.getPlacaVeiculo() != null && dados.getPlacaVeiculo().getValor() != null) {
            return dados.getPlacaVeiculo().getValor();
        }
        if (dados.getModeloVeiculo() != null && dados.getModeloVeiculo().getValor() != null) {
            return dados.getModeloVeiculo().getValor();
        }
        return null;
    }
}
