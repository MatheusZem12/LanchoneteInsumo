package br.com.insumo.lanchonete.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsumoDto {
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "codigo")
    private String codigo;

    @JsonProperty(value = "nome")
    private String nome;

    @JsonProperty(value = "descricao")
    private String descricao;

    @JsonProperty(value = "quantidade_critica")
    private Integer quantidadeCritica;
}
