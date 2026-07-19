package com.residencial.mapper;

import com.residencial.dto.response.ComunicadoResponse;
import com.residencial.entity.Comunicado;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversión entre Comunicado (entidad) y ComunicadoResponse (DTO).
 */
@Component
public class ComunicadoMapper {

    public ComunicadoResponse toResponse(Comunicado comunicado) {
        if (comunicado == null) return null;
        return ComunicadoResponse.builder()
                .idComunicado(comunicado.getIdComunicado())
                .titulo(comunicado.getTitulo())
                .mensaje(comunicado.getMensaje())
                .prioridad(comunicado.getPrioridad() != null ? comunicado.getPrioridad().name() : null)
                .fechaVencimiento(comunicado.getFechaVencimiento())
                .createdAt(comunicado.getCreatedAt())
                .build();
    }
}
