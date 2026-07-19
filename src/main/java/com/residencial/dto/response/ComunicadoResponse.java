package com.residencial.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para un comunicado.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ComunicadoResponse {
    private Long idComunicado;
    private String titulo;
    private String mensaje;
    private String prioridad;
    private LocalDate fechaVencimiento;
    private LocalDateTime createdAt;
}
