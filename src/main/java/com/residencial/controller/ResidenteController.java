package com.residencial.controller;

import com.residencial.dto.request.ResidenteRequest;
import com.residencial.dto.response.ResidenteResponse;
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
 * Controlador REST para gestión de residentes.
 *
 * Permisos:
 *  ADMINISTRADOR → CRUD completo.
 *  RESIDENTE     → GET /me  (sus propios datos).
 */
@RestController
@RequestMapping("/api/residentes")
@RequiredArgsConstructor
@Tag(name = "Residentes", description = "CRUD de residentes del condominio")
@SecurityRequirement(name = "BearerAuth")
public class ResidenteController {

    private final ResidenteService residenteService;

    // ── ADMINISTRADOR ─────────────────────────────────────────────────────────

    @Operation(summary = "Listar todos los residentes [ADMIN]")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ResidenteResponse>> listarTodos() {
        return ResponseEntity.ok(residenteService.listarTodos());
    }

    @Operation(summary = "Obtener residente por ID [ADMIN]")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ResidenteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(residenteService.buscarPorId(id));
    }

    @Operation(summary = "Crear un nuevo residente [ADMIN]")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ResidenteResponse> crear(@Valid @RequestBody ResidenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(residenteService.crear(request));
    }

    @Operation(summary = "Actualizar un residente existente [ADMIN]")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ResidenteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResidenteRequest request) {
        return ResponseEntity.ok(residenteService.actualizar(id, request));
    }

    @Operation(summary = "Desactivar un residente [ADMIN]")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        residenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ── RESIDENTE (/me) ───────────────────────────────────────────────────────

    @Operation(
        summary = "Obtener mi perfil [RESIDENTE]",
        description = "Retorna el perfil del residente vinculado al usuario JWT autenticado"
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public ResponseEntity<ResidenteResponse> obtenerMiPerfil() {
        return ResponseEntity.ok(residenteService.obtenerMiPerfil());
    }
}
