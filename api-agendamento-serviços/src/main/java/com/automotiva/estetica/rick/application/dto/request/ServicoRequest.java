package com.automotiva.estetica.rick.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
public class ServicoRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    private BigDecimal preco;

    private String imagem;

    @NotNull(message = "A duração em horas é obrigatória")
    @Min(value = 1, message = "A duração deve ser de pelo menos 1 hora")
    private Integer duracaoHoras;

    @NotNull(message = "A categoria é obrigatória")
    private Long categoriaId;
}
