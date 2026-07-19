package com.residencial.service;

import com.residencial.dto.request.LoginRequest;
import com.residencial.dto.response.LoginResponse;
import com.residencial.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación.
 * Delega la validación de credenciales a Spring Security y genera el JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Autentica al usuario y genera un JWT con sus roles.
     *
     * @param request credenciales (usuario + password)
     * @return LoginResponse con token, nombre de usuario y roles
     */
    public LoginResponse login(LoginRequest request) {
        // 1. Autenticar contra Spring Security (bcrypt + UserDetailsService)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsuario(), request.getPassword())
        );

        // 2. Generar JWT
        String token = tokenProvider.generateToken(authentication);

        // 3. Obtener roles del principal autenticado
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 4. Obtener ID del usuario desde el principal
        com.residencial.security.CustomUserDetails userDetails =
                (com.residencial.security.CustomUserDetails) authentication.getPrincipal();

        return LoginResponse.builder()
                .token(token)
                .usuario(userDetails.getUsername())
                .idUsuario(userDetails.getIdUsuario())
                .roles(roles)
                .build();
    }
}
