package com.residencial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencial.config.SecurityConfig;
import com.residencial.dto.request.LoginRequest;
import com.residencial.dto.request.ResidenteRequest;
import com.residencial.dto.response.LoginResponse;
import com.residencial.security.CustomUserDetailsService;
import com.residencial.security.JwtAuthenticationFilter;
import com.residencial.security.JwtTokenProvider;
import com.residencial.service.AuthService;
import com.residencial.service.PagoService;
import com.residencial.service.ResidenteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, ResidenteController.class, PagoController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("Pruebas de Seguridad y Autorización por Roles")
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private ResidenteService residenteService;

    @MockBean
    private PagoService pagoService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    @Test
    @DisplayName("Login administrador correcto")
    void login_admin_correcto() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsuario("admin");
        request.setPassword("Admin2024!");

        LoginResponse response = LoginResponse.builder()
                .token("jwt-mock")
                .usuario("admin")
                .idUsuario(1L)
                .roles(List.of("ROLE_ADMINISTRADOR"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-mock"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMINISTRADOR"));
    }

    @Test
    @DisplayName("Login residente correcto")
    void login_residente_correcto() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsuario("residente1");
        request.setPassword("Admin2024!");

        LoginResponse response = LoginResponse.builder()
                .token("jwt-mock-residente")
                .usuario("residente1")
                .idUsuario(2L)
                .roles(List.of("ROLE_RESIDENTE"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-mock-residente"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_RESIDENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Administrador puede crear residente")
    void admin_puede_crear_residente() throws Exception {
        ResidenteRequest request = new ResidenteRequest();
        request.setNombres("Juan");
        request.setApellidos("Perez");
        request.setCedula("1700000000");
        request.setTelefono("0999999999");

        mockMvc.perform(post("/api/residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "RESIDENTE")
    @DisplayName("Residente NO puede crear residente (403 Forbidden)")
    void residente_no_puede_crear_residente() throws Exception {
        ResidenteRequest request = new ResidenteRequest();
        request.setNombres("Juan");
        request.setApellidos("Perez");
        request.setCedula("1700000000");
        request.setTelefono("0999999999");

        mockMvc.perform(post("/api/residentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RESIDENTE", username = "residente1")
    @DisplayName("Residente puede consultar sus pagos (/me)")
    void residente_puede_consultar_sus_pagos() throws Exception {
        mockMvc.perform(get("/api/pagos/me"))
                .andExpect(status().isOk());
    }
}
