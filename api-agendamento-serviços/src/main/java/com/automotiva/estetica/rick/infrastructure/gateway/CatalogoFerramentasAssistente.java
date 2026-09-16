package com.automotiva.estetica.rick.infrastructure.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Declarações fixas das tools que o assistente do gerente pode chamar
 * (formato Gemini functionDeclarations). Ficam no backend — nunca são
 * enviadas pelo app — para que o app não possa alterar o que a IA tem
 * permissão de fazer.
 */
final class CatalogoFerramentasAssistente {

    private CatalogoFerramentasAssistente() {
    }

    static ArrayNode declaracoes(ObjectMapper mapper) {
        ArrayNode tools = mapper.createArrayNode();
        ArrayNode funcoes = tools.addObject().putArray("functionDeclarations");

        ObjectNode buscarServicos = funcoes.addObject();
        buscarServicos.put("name", "buscarServicos");
        buscarServicos.put("description",
                "Pesquisa serviços reais oferecidos pela Rick Estética Automotiva por termo (nome, descrição ou "
                        + "categoria). Nunca invente serviços — só use o que essa tool retornar.");
        ObjectNode buscarServicosParams = buscarServicos.putObject("parameters");
        buscarServicosParams.put("type", "OBJECT");
        ObjectNode buscarServicosProps = buscarServicosParams.putObject("properties");
        buscarServicosProps.putObject("termo").put("type", "STRING").put("description",
                "Termo de busca, ex: 'polimento de farol'");
        buscarServicosParams.putArray("required").add("termo");

        ObjectNode buscarVeiculos = funcoes.addObject();
        buscarVeiculos.put("name", "buscarVeiculos");
        buscarVeiculos.put("description",
                "Lista os veículos reais cadastrados de qualquer cliente (não do gerente que está usando o "
                        + "assistente). Use para identificar o veículo do cliente que o gerente está atendendo, "
                        + "casando com placa, marca ou modelo informados na conversa.");
        ObjectNode buscarVeiculosParams = buscarVeiculos.putObject("parameters");
        buscarVeiculosParams.put("type", "OBJECT");
        ObjectNode buscarVeiculosProps = buscarVeiculosParams.putObject("properties");
        buscarVeiculosProps.putObject("termo").put("type", "STRING").put("description",
                "Placa, marca ou modelo do veículo do cliente, conforme descrito na conversa (ex: 'ABC1234', 'Corsa')");

        ObjectNode buscarHorarios = funcoes.addObject();
        buscarHorarios.put("name", "buscarHorariosDisponiveis");
        buscarHorarios.put("description",
                "Consulta os horários realmente disponíveis para agendar os serviços informados, em uma data "
                        + "específica. Nunca invente horários — só use o que essa tool retornar.");
        ObjectNode buscarHorariosParams = buscarHorarios.putObject("parameters");
        buscarHorariosParams.put("type", "OBJECT");
        ObjectNode buscarHorariosProps = buscarHorariosParams.putObject("properties");
        buscarHorariosProps.putObject("data").put("type", "STRING").put("description", "Data no formato YYYY-MM-DD");
        ObjectNode servicosIdsSchema = buscarHorariosProps.putObject("servicosIds");
        servicosIdsSchema.put("type", "ARRAY");
        servicosIdsSchema.putObject("items").put("type", "INTEGER");
        servicosIdsSchema.put("description", "IDs dos serviços já identificados via buscarServicos");
        buscarHorariosParams.putArray("required").add("data").add("servicosIds");

        ObjectNode criarAgendamento = funcoes.addObject();
        criarAgendamento.put("name", "criarAgendamento");
        criarAgendamento.put("description",
                "Propõe a criação de um agendamento real com os dados já confirmados (serviço, veículo, data e "
                        + "horário). Só chame depois de ter certeza de todos os dados via as outras tools. A criação "
                        + "de fato só acontece depois da confirmação explícita do usuário na interface — considere "
                        + "isso uma proposta, não uma execução.");
        ObjectNode criarAgendamentoParams = criarAgendamento.putObject("parameters");
        criarAgendamentoParams.put("type", "OBJECT");
        ObjectNode criarAgendamentoProps = criarAgendamentoParams.putObject("properties");
        criarAgendamentoProps.putObject("servicoId").put("type", "INTEGER").put("description",
                "ID do serviço encontrado via buscarServicos");
        criarAgendamentoProps.putObject("veiculoId").put("type", "INTEGER").put("description",
                "ID do veículo do cliente, encontrado via buscarVeiculos");
        criarAgendamentoProps.putObject("data").put("type", "STRING").put("description", "Data no formato YYYY-MM-DD");
        criarAgendamentoProps.putObject("horario").put("type", "STRING").put("description",
                "Horário no formato HH:mm, escolhido entre os retornados por buscarHorariosDisponiveis");
        criarAgendamentoProps.putObject("precoMinimo").put("type", "NUMBER").put("description",
                "Valor total estimado do(s) serviço(s), somado a partir dos preços retornados por buscarServicos");
        criarAgendamentoParams.putArray("required").add("servicoId").add("veiculoId").add("data").add("horario");

        return tools;
    }
}
