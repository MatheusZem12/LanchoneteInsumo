package br.com.insumo.lanchonete.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.insumo.lanchonete.models.Usuario;

public class UsuarioDto {

    public static class Views {
        public static class Public {}
        public static class Detailed extends Public {}
        public static class Admin extends Detailed {}
    }

    
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "senha")
    private String senha;

    @JsonProperty(value = "role")
    private List<String> role;

    public UsuarioDto(Usuario entity){
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.senha = entity.getSenha();
        this.role = entity.getUsuarioRoleList()
                .stream()
                .map(userRole -> userRole.getRole().getNome())
                .toList();
    }
}
