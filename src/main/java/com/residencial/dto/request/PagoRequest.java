package com.residencial.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO de entrada para registrar un pago.
 */
@Getter @Setter
@Schema(description = "Datos de un pago")
public class PagoRequest {

    @NotNull(message = "El ID de cuota es obligatorio")
    private Long idCuota;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @DecimalMax(value = "500.00", message = "El monto no puede superar $500")
    @Schema(description = "Monto a pagar en dólares", example = "80.00")
    private BigDecimal montoPagado;
}
