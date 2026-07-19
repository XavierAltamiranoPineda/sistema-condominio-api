package com.residencial.mapper;

import com.residencial.dto.response.PagoResponse;
import com.residencial.entity.Pago;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversión entre Pago (entidad) y PagoResponse (DTO).
 */
@Component
public class PagoMapper {

    public PagoResponse toResponse(Pago pago) {
        if (pago == null) return null;
        return PagoResponse.builder()
                .idPago(pago.getIdPago())
                .idCuota(pago.getCuota().getIdCuota())
                .codigoCasa(pago.getCuota().getResidencia().getCodigoCasa())
                .mes(pago.getCuota().getMes())
                .anio(pago.getCuota().getAnio())
                .fechaPago(pago.getFechaPago())
                .montoPagado(pago.getMontoPagado())
                .estado(pago.getEstado() != null ? pago.getEstado().name() : null)
                .build();
    }
}
