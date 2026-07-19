package com.residencial.dto.response;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO de respuesta para una cuota mensual.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CuotaResponse {
    private Long idCuota;
    private Long idResidencia;
    private String codigoCasa;
    private Short mes;
    private Short anio;
    private BigDecimal valor;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
}
