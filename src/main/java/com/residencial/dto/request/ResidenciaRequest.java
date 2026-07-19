package com.residencial.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO de entrada para crear o actualizar una residencia.
 *
 * CORRECCIÓN: idPropietario es ahora OBLIGATORIO (NOT NULL en BD).
 */
@Getter @Setter
@Schema(description = "Datos de una residencia")
public class ResidenciaRequest {

    /** ID del residente propietario — obligatorio */
    @NotNull(message = "El ID del propietario es obligatorio")
    @Schema(description = "ID del residente propietario", example = "1")
    private Long idPropietario;

    @NotBlank(message = "El código de casa es obligatorio")
    @Size(max = 20, message = "El código de casa no puede superar 20 caracteres")
    @Schema(description = "Código único de la casa", example = "CASA-001")
    private String codigoCasa;

    @NotNull(message = "La cuota mensual es obligatoria")
    @DecimalMin(value = "0.01", inclusive = true, message = "La cuota mensual debe ser mayor a 0")
    @DecimalMax(value = "500.00", message = "La cuota mensual no puede superar $500")
    @Schema(description = "Cuota mensual en dólares", example = "80.00")
    private BigDecimal cuotaMensual;
}
