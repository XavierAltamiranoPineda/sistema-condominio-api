package com.residencial.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementación personalizada de UserDetails que expone el ID del usuario
 * para incluirlo en el JWT y en los contextos de seguridad.
 */
public class CustomUserDetails implements UserDetails {

    private final Long idUsuario;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Long idUsuario, String username, String password,
                             boolean enabled, List<GrantedAuthority> authorities) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    @Override public String getUsername()  { return username; }
    @Override public String getPassword()  { return password; }
    @Override public boolean isEnabled()   { return enabled; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
