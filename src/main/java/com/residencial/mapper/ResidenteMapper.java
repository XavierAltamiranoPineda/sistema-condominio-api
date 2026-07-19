package com.residencial.mapper;

import com.residencial.dto.response.ResidenteResponse;
import com.residencial.entity.Residente;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversión entre Residente (entidad) y ResidenteResponse (DTO).
 */
@Component
public class ResidenteMapper {

    public ResidenteResponse toResponse(Residente residente) {
        if (residente == null) return null;
        return ResidenteResponse.builder()
                .idResidente(residente.getIdResidente())
                .nombres(residente.getNombres())
                .apellidos(residente.getApellidos())
                .cedula(residente.getCedula())
                .telefono(residente.getTelefono())
                .estado(residente.getEstado() != null ? residente.getEstado().name() : null)
                .createdAt(residente.getCreatedAt())
                .build();
    }
}
