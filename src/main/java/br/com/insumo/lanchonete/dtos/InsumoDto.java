package br.com.insumo.lanchonete.dtos;

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
public class InsumoDto {
    @JsonProperty(value = "id")
    private Long id;

    @NotBlank(message = "Código é obrigatório")
    @JsonProperty(value = "codigo")
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
    @JsonProperty(value = "nome")
    private String nome;

    @JsonProperty(value = "descricao")
    private String descricao;

    @NotNull(message = "Quantidade crítica é obrigatória")
    @Min(value = 0, message = "Quantidade crítica deve ser no mínimo 0")
    @JsonProperty(value = "quantidade_critica")
    private Integer quantidadeCritica;

    @JsonProperty(value = "quantidade_estoque")
    private Integer quantidadeEstoque;
}
