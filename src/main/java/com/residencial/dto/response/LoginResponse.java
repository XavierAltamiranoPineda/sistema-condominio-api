package com.residencial.dto.response;

import lombok.*;
import java.util.List;

/**
 * DTO de respuesta para el login exitoso.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private String token;
    private String usuario;
    private Long idUsuario;
    private List<String> roles;
}
