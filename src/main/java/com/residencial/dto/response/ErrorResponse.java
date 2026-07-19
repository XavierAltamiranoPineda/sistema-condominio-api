package com.residencial.dto.response;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO estándar para errores de la API.
 * Formato consistente para todos los errores HTTP.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String mensaje;
    private String path;
}
