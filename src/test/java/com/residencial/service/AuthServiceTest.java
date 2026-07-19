package com.residencial.service;

import com.residencial.dto.request.LoginRequest;
import com.residencial.dto.response.LoginResponse;
import com.residencial.security.CustomUserDetails;
import com.residencial.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas unitarias")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private Authentication authentication;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsuario("admin");
        loginRequest.setPassword("Admin2024!");

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));

        userDetails = new CustomUserDetails(
                1L, "admin", "hashedPassword", true,
                authorities
        );

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        doReturn(authorities).when(authentication).getAuthorities();
    }

    @Test
    @DisplayName("Login exitoso debe retornar token y roles")
    void login_credencialesValidas_retornaLoginResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token-mock");

        LoginResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-mock");
        assertThat(response.getUsuario()).isEqualTo("admin");
        assertThat(response.getIdUsuario()).isEqualTo(1L);
        assertThat(response.getRoles()).contains("ROLE_ADMINISTRADOR");
    }

    @Test
    @DisplayName("Login con credenciales incorrectas debe lanzar BadCredentialsException")
    void login_credencialesIncorrectas_lanzaExcepcion() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }
}
