package com.residencial.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO de entrada para crear una cuota mensual.
 */
@Getter @Setter
@Schema(description = "Datos de una cuota mensual")
public class CuotaRequest {

    @NotNull(message = "El ID de residencia es obligatorio")
    private Long idResidencia;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    @Schema(description = "Mes de la cuota (1-12)", example = "7")
    private Short mes;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año mínimo permitido es 2000")
    @Schema(description = "Año de la cuota", example = "2026")
    private Short anio;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.00", inclusive = false, message = "El valor debe ser mayor a 0")
    @DecimalMax(value = "500.00", message = "El valor no puede superar $500")
    private BigDecimal valor;
}
