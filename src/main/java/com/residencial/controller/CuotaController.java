package com.residencial.controller;

import com.residencial.dto.request.CuotaRequest;
import com.residencial.dto.response.CuotaResponse;
import com.residencial.entity.Residente;
import com.residencial.service.CuotaService;
import com.residencial.service.ResidenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de cuotas mensuales.
 *
 * Permisos:
 *  ADMINISTRADOR → CRUD completo + consulta general.
 *  RESIDENTE     → GET /me (sus propias cuotas), GET /residencia/{id}.
 */
@RestController
@RequestMapping("/api/cuotas")
@RequiredArgsConstructor
@Tag(name = "Cuotas", description = "Gestión de cuotas mensuales por residencia")
@SecurityRequirement(name = "BearerAuth")
public class CuotaController {

    private final CuotaService     cuotaService;
    private final ResidenteService residenteService;

    // ── ADMINISTRADOR ─────────────────────────────────────────────────────────

    @Operation(summary = "Listar todas las cuotas [ADMIN]")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<CuotaResponse>> listarTodas() {
        return ResponseEntity.ok(cuotaService.listarTodas());
    }

    @Operation(summary = "Listar cuotas por residencia [ADMIN/RESIDENTE]")
    @GetMapping("/residencia/{idResidencia}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public ResponseEntity<List<CuotaResponse>> listarPorResidencia(@PathVariable Long idResidencia) {
        return ResponseEntity.ok(cuotaService.listarPorResidencia(idResidencia));
    }

    @Operation(summary = "Crear una cuota mensual [ADMIN]")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<CuotaResponse> crear(@Valid @RequestBody CuotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuotaService.crear(request));
    }

    // ── RESIDENTE (/me) ───────────────────────────────────────────────────────

    @Operation(
        summary = "Mis cuotas [RESIDENTE]",
        description = "Retorna las cuotas de las residencias donde vive el residente autenticado"
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public ResponseEntity<List<CuotaResponse>> listarMisCuotas() {
        Residente residente = residenteService.obtenerResidenteAutenticado();
        return ResponseEntity.ok(cuotaService.listarMisCuotas(residente.getIdResidente()));
    }
}
