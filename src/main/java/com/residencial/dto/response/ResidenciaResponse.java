package com.residencial.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para una residencia.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ResidenciaResponse {
    private Long idResidencia;
    private Long idPropietario;
    private String nombrePropietario;
    private String codigoCasa;
    private BigDecimal cuotaMensual;
    private String estado;
    private LocalDateTime createdAt;
}
