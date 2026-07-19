package com.residencial.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para un pago.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PagoResponse {
    private Long idPago;
    private Long idCuota;
    private String codigoCasa;
    private Short mes;
    private Short anio;
    private LocalDate fechaPago;
    private BigDecimal montoPagado;
    private String estado;
}
