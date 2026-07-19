package com.residencial.controller;

import com.residencial.dto.request.LoginRequest;
import com.residencial.dto.response.LoginResponse;
import com.residencial.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de login y gestión de sesión")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Autentica el usuario y retorna un JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
