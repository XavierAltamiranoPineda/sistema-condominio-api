package com.residencial.service;

import com.residencial.dto.request.PagoRequest;
import com.residencial.dto.response.PagoResponse;
import com.residencial.entity.Cuota;
import com.residencial.entity.Pago;
import com.residencial.exception.BusinessException;
import com.residencial.exception.ResourceNotFoundException;
import com.residencial.mapper.PagoMapper;
import com.residencial.repository.CuotaRepository;
import com.residencial.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de pagos.
 * Reglas de negocio:
 *  - No pagos negativos.
 *  - No pagar más que la deuda pendiente.
 *  - Pagos parciales permitidos.
 *  - Monto máximo por pago: $500.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {

    private final PagoRepository pagoRepository;
    private final CuotaRepository cuotaRepository;
    private final PagoMapper pagoMapper;

    @Transactional(readOnly = true)
    public List<PagoResponse> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(pagoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorCuota(Long idCuota) {
        return pagoRepository.findByCuota_IdCuota(idCuota).stream()
                .map(pagoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna los pagos realizados en residencias donde vive el residente autenticado.
     * Usado en GET /api/pagos/me.
     *
     * @param idResidente ID del residente autenticado
     */
    @Transactional(readOnly = true)
    public List<PagoResponse> listarMisPagos(Long idResidente) {
        return pagoRepository.findByIdResidente(idResidente).stream()
                .map(pagoMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PagoResponse registrar(PagoRequest request) {
        Cuota cuota = cuotaRepository.findById(request.getIdCuota())
                .orElseThrow(() -> new ResourceNotFoundException("Cuota", request.getIdCuota()));

        BigDecimal monto = request.getMontoPagado();

        // Validación: monto positivo
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto del pago debe ser mayor a 0");
        }

        // Calcular deuda actual
        BigDecimal totalPagado = pagoRepository.sumMontoPagadoByIdCuota(cuota.getIdCuota());
        BigDecimal deudaPendiente = cuota.getValor().subtract(totalPagado);

        if (deudaPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La cuota ya se encuentra completamente pagada");
        }

        // No permitir pagar más que la deuda pendiente
        if (monto.compareTo(deudaPendiente) > 0) {
            throw new BusinessException(
                    "El monto supera la deuda pendiente de $" + deudaPendiente);
        }

        // Determinar estado del pago
        BigDecimal nuevoTotal = totalPagado.add(monto);
        Pago.EstadoPago estado = nuevoTotal.compareTo(cuota.getValor()) >= 0
                ? Pago.EstadoPago.COMPLETADO
                : Pago.EstadoPago.PARCIAL;

        Pago pago = Pago.builder()
                .cuota(cuota)
                .montoPagado(monto)
                .estado(estado)
                .build();

        return pagoMapper.toResponse(pagoRepository.save(pago));
    }
}
