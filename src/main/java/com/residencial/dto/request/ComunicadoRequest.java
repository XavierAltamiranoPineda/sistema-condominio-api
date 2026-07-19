package com.residencial.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de entrada para crear un comunicado.
 */
@Getter @Setter
@Schema(description = "Datos de un comunicado")
public class ComunicadoRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede superar 200 caracteres")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @Pattern(regexp = "ALTA|NORMAL|BAJA", message = "Prioridad debe ser ALTA, NORMAL o BAJA")
    private String prioridad;

    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;

    /** IDs de residentes destinatarios (vacío = enviar a todos) */
    private List<Long> destinatarios;
}
