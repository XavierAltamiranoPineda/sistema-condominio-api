package com.residencial.mapper;

import com.residencial.dto.response.CuotaResponse;
import com.residencial.entity.Cuota;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Mapper para conversión entre Cuota (entidad) y CuotaResponse (DTO).
 * Recibe el monto pagado como parámetro externo (calculado desde PagoRepository).
 */
@Component
public class CuotaMapper {

    public CuotaResponse toResponse(Cuota cuota, BigDecimal montoPagado) {
        if (cuota == null) return null;

        BigDecimal pagado = montoPagado != null ? montoPagado : BigDecimal.ZERO;
        BigDecimal saldo = cuota.getValor().subtract(pagado);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;

        return CuotaResponse.builder()
                .idCuota(cuota.getIdCuota())
                .idResidencia(cuota.getResidencia().getIdResidencia())
                .codigoCasa(cuota.getResidencia().getCodigoCasa())
                .mes(cuota.getMes())
                .anio(cuota.getAnio())
                .valor(cuota.getValor())
                .montoPagado(pagado)
                .saldoPendiente(saldo)
                .build();
    }
}
