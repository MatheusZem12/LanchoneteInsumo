package br.com.insumo.lanchonete.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoInsumoDto {
    @JsonProperty(value = "id")
    private Long id;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    @JsonProperty(value = "quantidade")
    private Integer quantidade;

    @NotBlank(message = "Tipo de movimentação é obrigatório")
    @JsonProperty(value = "tipo_movimentacao")
    private String tipoMovimentacao;

    @JsonProperty(value = "data")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime data;

    @NotBlank(message = "ID do usuário é obrigatório")
    @JsonProperty(value = "usuario_id")
    private String usuarioId;

    @NotNull(message = "ID do insumo é obrigatório")
    @JsonProperty(value = "insumo_id")
    private Long insumoId;
}
