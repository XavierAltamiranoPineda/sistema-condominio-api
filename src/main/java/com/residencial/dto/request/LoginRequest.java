package com.residencial.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para el login de usuario.
 */
@Getter @Setter
@Schema(description = "Credenciales de autenticación")
public class LoginRequest {

    @NotBlank(message = "El usuario es obligatorio")
    @Schema(description = "Nombre de usuario", example = "admin")
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña", example = "Admin2024!")
    private String password;
}
