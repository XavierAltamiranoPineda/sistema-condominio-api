package com.residencial.controller;

import com.residencial.dto.request.AsignacionRequest;
import com.residencial.service.ResidenteResidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
@Tag(name = "Asignaciones", description = "Asignación de residentes a residencias")
@SecurityRequirement(name = "BearerAuth")
public class ResidenteResidenciaController {

    private final ResidenteResidenciaService asignacionService;

    @Operation(summary = "Asignar un residente a una residencia [ADMIN]")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> asignar(@Valid @RequestBody AsignacionRequest request) {
        asignacionService.asignar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Asignación exitosa"));
    }
}
