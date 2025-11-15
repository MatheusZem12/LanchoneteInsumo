package br.com.insumo.lanchonete.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.insumo.lanchonete.models.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDto {

    public static class Views {
        public static class Public {}
        public static class Detailed extends Public {}
        public static class Admin extends Detailed {}
    }

    
    @JsonProperty(value = "id")
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @JsonProperty(value = "nome")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "senha")
    private String senha;

    @JsonProperty(value = "telefone")
    private String telefone;

    @JsonProperty(value = "role")
    private List<String> role;

    public UsuarioDto(Usuario entity){
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.email = entity.getEmail();
        this.senha = entity.getSenha();
        this.telefone = entity.getTelefone();
        this.role = entity.getUsuarioRoleList()
                .stream()
                .map(userRole -> userRole.getRole().getNome())
                .toList();
    }
}
