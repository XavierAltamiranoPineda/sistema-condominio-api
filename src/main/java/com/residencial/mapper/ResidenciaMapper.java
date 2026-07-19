package com.residencial.mapper;

import com.residencial.dto.response.ResidenciaResponse;
import com.residencial.entity.Residencia;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversión entre Residencia (entidad) y ResidenciaResponse (DTO).
 * El propietario es siempre obligatorio (NOT NULL), pero se mantiene null-safe
 * por compatibilidad en caso de datos históricos previos a la migración.
 */
@Component
public class ResidenciaMapper {

    public ResidenciaResponse toResponse(Residencia residencia) {
        if (residencia == null) return null;

        Long idPropietario = null;
        String nombrePropietario = null;

        // Propietario es obligatorio; null-safe por robustez
        if (residencia.getPropietario() != null) {
            idPropietario = residencia.getPropietario().getIdResidente();
            nombrePropietario = residencia.getPropietario().getNombres()
                    + " " + residencia.getPropietario().getApellidos();
        }

        return ResidenciaResponse.builder()
                .idResidencia(residencia.getIdResidencia())
                .idPropietario(idPropietario)
                .nombrePropietario(nombrePropietario)
                .codigoCasa(residencia.getCodigoCasa())
                .cuotaMensual(residencia.getCuotaMensual())
                .estado(residencia.getEstado() != null ? residencia.getEstado().name() : null)
                .createdAt(residencia.getCreatedAt())
                .build();
    }
}
