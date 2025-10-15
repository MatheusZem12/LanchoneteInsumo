package br.com.insumo.lanchonete.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoInsumoDto {
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "quantidade")
    private Integer quantidade;

    @JsonProperty(value = "tipo_movimentacao")
    private String tipoMovimentacao;

    @JsonProperty(value = "data")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime data;

    @JsonProperty(value = "usuario_id")
    private String usuarioId;

    @JsonProperty(value = "insumo_id")
    private Long insumoId;
}
