package com.automotiva.estetica.rick.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentosHojeListResponse {

    private List<AgendamentoHojeResponse> data;
    private Integer total;
    private LocalDateTime timestamp;
}
