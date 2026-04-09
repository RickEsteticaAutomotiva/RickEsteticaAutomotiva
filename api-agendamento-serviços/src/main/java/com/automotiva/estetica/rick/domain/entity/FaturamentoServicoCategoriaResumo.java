package com.automotiva.estetica.rick.domain.entity;

import java.util.List;

public record FaturamentoServicoCategoriaResumo(String categoria, List<FaturamentoServicoItemResumo> servicos) {
}
