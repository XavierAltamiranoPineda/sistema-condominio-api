package com.residencial.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta para el reporte general del sistema.
 */
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ReporteResponse {

    /** "OK" si no hay novedades, "ALERTA" si hay problemas */
    private String estado;

    /** Mensaje simple cuando estado es OK */
    private String mensaje;

    /** Lista de novedades detectadas (solo cuando estado=ALERTA) */
    private List<Novedad> novedades;

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class Novedad {
        private String tipo;
        private String descripcion;
        private Object datos;
    }
}
