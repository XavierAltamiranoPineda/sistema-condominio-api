package com.residencial.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "Datos para asignar un residente a una residencia")
public class AsignacionRequest {

    @NotNull(message = "El ID del residente es obligatorio")
    @Schema(description = "ID del residente a asignar", example = "1")
    private Long idResidente;

    @NotNull(message = "El ID de la residencia es obligatorio")
    @Schema(description = "ID de la residencia", example = "2")
    private Long idResidencia;

    @Schema(description = "Parentesco o rol en la residencia", example = "Hijo")
    private String parentesco;
}
