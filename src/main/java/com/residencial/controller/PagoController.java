package com.residencial.controller;

import com.residencial.dto.request.PagoRequest;
import com.residencial.dto.response.PagoResponse;
import com.residencial.entity.Residente;
import com.residencial.service.PagoService;
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
 * Controlador REST para gestión de pagos.
 *
 * Permisos:
 *  ADMINISTRADOR → listar todos, listar por cuota, registrar pago.
 *  RESIDENTE     → GET /me (sus propios pagos).
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Registro y consulta de pagos de cuotas")
@SecurityRequirement(name = "BearerAuth")
public class PagoController {

    private final PagoService      pagoService;
    private final ResidenteService residenteService;

    // ── ADMINISTRADOR ─────────────────────────────────────────────────────────

    @Operation(summary = "Listar todos los pagos [ADMIN]")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<PagoResponse>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @Operation(summary = "Listar pagos por cuota [ADMIN]")
    @GetMapping("/cuota/{idCuota}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<PagoResponse>> listarPorCuota(@PathVariable Long idCuota) {
        return ResponseEntity.ok(pagoService.listarPorCuota(idCuota));
    }

    @Operation(summary = "Registrar un pago (completo o parcial) [ADMIN]")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PagoResponse> registrar(@Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrar(request));
    }

    // ── RESIDENTE (/me) ───────────────────────────────────────────────────────

    @Operation(
        summary = "Mis pagos [RESIDENTE]",
        description = "Retorna los pagos de las residencias donde vive el residente autenticado"
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','RESIDENTE')")
    public ResponseEntity<List<PagoResponse>> listarMisPagos() {
        Residente residente = residenteService.obtenerResidenteAutenticado();
        return ResponseEntity.ok(pagoService.listarMisPagos(residente.getIdResidente()));
    }
}
