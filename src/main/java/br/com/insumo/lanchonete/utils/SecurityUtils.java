package br.com.insumo.lanchonete.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.insumo.lanchonete.models.Usuario;

@Component
public class SecurityUtils {

    /**
     * Verifica se o usuário autenticado é admin
     */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Verifica se o usuário autenticado é o dono do recurso ou é admin
     */
    public boolean isOwnerOrAdmin(Long userId) {
        if (isAdmin()) return true;
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return false;
        
        Usuario currentUser = (Usuario) auth.getPrincipal();
        return currentUser.getId() != null && currentUser.getId().equals(userId);
    }

    /**
     * Obtém o ID do usuário autenticado
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        
        Usuario currentUser = (Usuario) auth.getPrincipal();
        return currentUser.getId();
    }

    /**
     * Obtém o usuário autenticado
     */
    public Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        
        return (Usuario) auth.getPrincipal();
    }
}
