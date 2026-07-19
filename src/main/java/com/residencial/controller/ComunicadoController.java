package com.residencial.controller;

import com.residencial.dto.request.ComunicadoRequest;
import com.residencial.dto.response.ComunicadoResponse;
import com.residencial.entity.Residente;
import com.residencial.service.ComunicadoService;
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
 * Controlador REST para gestión de comunicados.
 *
 * Permisos:
 *  ADMINISTRADOR → CRUD completo.
 *  RESIDENTE     → GET /me (sus comunicados recibidos), GET /{id}.
 */
@RestController
@RequestMapping("/api/comunicados")
@RequiredArgsConstructor
@Tag(name = "Comunicados", description = "Emisión y consulta de comunicados a residentes")
@SecurityRequirement(name = "BearerAuth")
public class ComunicadoController {

    private final ComunicadoService comunicadoService;
    private final ResidenteService  residenteService;

    // ── ADMINISTRADOR ─────────────────────────────────────────────────────────

    @Operation(summary = "Listar todos los comunicados [ADMIN]")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ComunicadoResponse>> listarTodos() {
        return ResponseEntity.ok(comunicadoService.listarTodos());
    }

    @Operation(summary = "Obtener comunicado por ID [ADMIN/RESIDENTE]")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public ResponseEntity<ComunicadoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comunicadoService.buscarPorId(id));
    }

    @Operation(summary = "Crear y distribuir un comunicado [ADMIN]")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ComunicadoResponse> crear(@Valid @RequestBody ComunicadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comunicadoService.crear(request));
    }

    // ── RESIDENTE (/me) ───────────────────────────────────────────────────────

    @Operation(
        summary = "Mis comunicados [RESIDENTE]",
        description = "Retorna los comunicados dirigidos al residente autenticado"
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public ResponseEntity<List<ComunicadoResponse>> listarMisComunicados() {
        Residente residente = residenteService.obtenerResidenteAutenticado();
        return ResponseEntity.ok(comunicadoService.listarMisComunicados(residente.getIdResidente()));
    }
}
