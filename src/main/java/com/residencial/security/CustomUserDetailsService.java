package com.residencial.security;

import com.residencial.entity.Usuario;
import com.residencial.entity.UsuarioRol;
import com.residencial.repository.UsuarioRepository;
import com.residencial.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetailsService que carga el usuario desde la base de datos
 * y construye un CustomUserDetails con sus roles asignados.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Obtener roles asignados al usuario
        List<UsuarioRol> usuarioRoles = usuarioRolRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());

        List<org.springframework.security.core.GrantedAuthority> authorities = usuarioRoles.stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre()))
                .collect(Collectors.toList());

        boolean enabled = usuario.getEstado() == Usuario.EstadoUsuario.ACTIVO;

        return new CustomUserDetails(
                usuario.getIdUsuario(),
                usuario.getUsuario(),
                usuario.getPasswordHash(),
                enabled,
                authorities
        );
    }
}
