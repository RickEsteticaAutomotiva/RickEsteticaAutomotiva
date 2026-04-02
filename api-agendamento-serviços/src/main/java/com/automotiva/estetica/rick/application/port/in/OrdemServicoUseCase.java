package com.automotiva.estetica.rick.application.port.in;

import com.automotiva.estetica.rick.application.dto.request.AdicionarServicosOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarStatusOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.AtualizarValorServicoOrdemRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoGestaoPageRequest;
import com.automotiva.estetica.rick.application.dto.request.OrdemServicoRequest;
import com.automotiva.estetica.rick.application.dto.request.PageRequest;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoDetalheResponse;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResponse;
import com.automotiva.estetica.rick.application.dto.response.HorarioDisponivelResponse;
import java.time.LocalDate;
import com.automotiva.estetica.rick.application.dto.response.OrdemServicoResumoResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface OrdemServicoUseCase {

    Page<OrdemServicoResponse> buscarTodos(PageRequest pageRequest);

    OrdemServicoResponse buscarPorId(Long id);

    List<OrdemServicoResponse> buscarPorUsuarioId(Long usuarioId);

    OrdemServicoResponse criar(OrdemServicoRequest request);

    OrdemServicoResponse atualizar(Long id, OrdemServicoRequest request);

    List<HorarioDisponivelResponse> buscarHorariosDisponiveis(LocalDate data, List<Long> servicosIds);

    Page<OrdemServicoResumoResponse> buscarTodosParaGestao(OrdemServicoGestaoPageRequest request);

    OrdemServicoDetalheResponse buscarDetalheParaGestao(Long ordemServicoId);

    OrdemServicoDetalheResponse atualizarStatusParaGestao(
            Long ordemServicoId, AtualizarStatusOrdemRequest request);

    OrdemServicoDetalheResponse adicionarServicosParaGestao(
            Long ordemServicoId, AdicionarServicosOrdemRequest request);

    OrdemServicoDetalheResponse atualizarValorServicoParaGestao(
            Long ordemServicoId, Long servicoId, AtualizarValorServicoOrdemRequest request);

    OrdemServicoDetalheResponse removerServicoParaGestao(Long ordemServicoId, Long servicoId);
}
