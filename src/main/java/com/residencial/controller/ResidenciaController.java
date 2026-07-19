package com.residencial.controller;

import com.residencial.dto.request.ResidenciaRequest;
import com.residencial.dto.response.ResidenciaResponse;
import com.residencial.entity.Residente;
import com.residencial.service.ResidenciaService;
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
 * Controlador REST para gestión de residencias.
 *
 * Permisos:
 *  ADMINISTRADOR → CRUD completo.
 *  RESIDENTE     → GET /me (sus propias residencias donde habita).
 */
@RestController
@RequestMapping("/api/residencias")
@RequiredArgsConstructor
@Tag(name = "Residencias", description = "CRUD de casas/residencias del condominio")
@SecurityRequirement(name = "BearerAuth")
public class ResidenciaController {

    private final ResidenciaService residenciaService;
    private final ResidenteService residenteService;

    // ── ADMINISTRADOR ─────────────────────────────────────────────────────────

    @Operation(summary = "Listar todas las residencias [ADMIN]")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ResidenciaResponse>> listarTodas() {
        return ResponseEntity.ok(residenciaService.listarTodas());
    }

    @Operation(summary = "Obtener residencia por ID [ADMIN]")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ResidenciaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(residenciaService.buscarPorId(id));
    }

    @Operation(summary = "Crear una nueva residencia [ADMIN]")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ResidenciaResponse> crear(@Valid @RequestBody ResidenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(residenciaService.crear(request));
    }

    @Operation(summary = "Actualizar una residencia existente [ADMIN]")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ResidenciaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResidenciaRequest request) {
        return ResponseEntity.ok(residenciaService.actualizar(id, request));
    }

    // ── RESIDENTE (/me) ───────────────────────────────────────────────────────

    @Operation(
        summary = "Mis residencias [RESIDENTE]",
        description = "Retorna las residencias donde habita el residente autenticado"
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public ResponseEntity<List<ResidenciaResponse>> listarMisResidencias() {
        Residente residente = residenteService.obtenerResidenteAutenticado();
        return ResponseEntity.ok(residenciaService.listarMisResidencias(residente.getIdResidente()));
    }
}
