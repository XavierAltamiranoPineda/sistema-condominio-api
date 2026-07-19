package com.residencial.controller;

import com.residencial.dto.response.ReporteResponse;
import com.residencial.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para reportes del sistema.
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reporte general del estado del condominio")
@SecurityRequirement(name = "BearerAuth")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Reporte general del sistema",
               description = "Retorna OK si no hay novedades, o ALERTA con detalles de problemas encontrados")
    @GetMapping("/general")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReporteResponse> reporteGeneral() {
        return ResponseEntity.ok(reporteService.generarReporteGeneral());
    }
}
