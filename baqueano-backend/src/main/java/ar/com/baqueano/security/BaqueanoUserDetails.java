package ar.com.baqueano.security;

import ar.com.baqueano.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador entre la entidad Usuario y el contrato UserDetails de Spring Security.
 * El authority es ROLE_<nombrePerfil> en mayusculas (ej. ROLE_ADMIN).
 * Los chequeos finos por accion (CREAR/EDITAR/...) los hace PermisoEvaluator.
 */
public class BaqueanoUserDetails implements UserDetails {

    private final Usuario usuario;

    public BaqueanoUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Long getUsuarioId() {
        return usuario.getId();
    }

    public Long getPerfilId() {
        return usuario.getPerfil().getId();
    }

    public String getPerfilNombre() {
        return usuario.getPerfil().getNombre();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().getNombre()));
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // El control fino por intentos_fallidos lo hace AuthService.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(usuario.getActivo());
    }
}
