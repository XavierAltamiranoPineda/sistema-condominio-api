package com.residencial.dto.response;

import com.residencial.entity.Residente;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para un residente.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ResidenteResponse {
    private Long idResidente;
    private String nombres;
    private String apellidos;
    private String cedula;
    private String telefono;
    private String estado;
    private LocalDateTime createdAt;
}
