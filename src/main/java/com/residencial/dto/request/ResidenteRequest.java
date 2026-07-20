package com.residencial.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para crear o actualizar un residente.
 */
@Getter @Setter
@Schema(description = "Datos de un residente")
public class ResidenteRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
    private String apellidos;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{10}", message = "La cédula debe tener exactamente 10 dígitos numéricos")
    @Schema(description = "Cédula ecuatoriana de 10 dígitos", example = "0912345678")
    private String cedula;

    @Size(max = 15, message = "El teléfono no puede superar 15 caracteres")
    private String telefono;

    @Schema(description = "Estado del residente", example = "ACTIVO", allowableValues = {"ACTIVO", "INACTIVO"})
    private com.residencial.entity.Residente.EstadoResidente estado;
}
