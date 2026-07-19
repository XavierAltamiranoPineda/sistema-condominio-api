package com.residencial.service;

import com.residencial.dto.request.PagoRequest;
import com.residencial.dto.response.PagoResponse;
import com.residencial.entity.Cuota;
import com.residencial.entity.Pago;
import com.residencial.entity.Residencia;
import com.residencial.exception.BusinessException;
import com.residencial.mapper.PagoMapper;
import com.residencial.repository.CuotaRepository;
import com.residencial.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoService - Pruebas unitarias")
class PagoServiceTest {

    @Mock private PagoRepository pagoRepository;
    @Mock private CuotaRepository cuotaRepository;
    @Mock private PagoMapper pagoMapper;

    @InjectMocks private PagoService pagoService;

    private Cuota cuota;
    private PagoRequest pagoRequest;

    @BeforeEach
    void setUp() {
        Residencia residencia = Residencia.builder()
                .idResidencia(1L)
                .codigoCasa("CASA-001")
                .build();

        cuota = Cuota.builder()
                .idCuota(1L)
                .residencia(residencia)
                .mes((short) 7)
                .anio((short) 2026)
                .valor(new BigDecimal("100.00"))
                .build();

        pagoRequest = new PagoRequest();
        pagoRequest.setIdCuota(1L);
        pagoRequest.setMontoPagado(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Registrar pago completo asigna estado COMPLETADO")
    void registrar_pagoCompleto_estadoCompletado() {
        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
        when(pagoRepository.sumMontoPagadoByIdCuota(1L)).thenReturn(BigDecimal.ZERO);
        Pago pagoGuardado = Pago.builder()
                .idPago(1L).cuota(cuota)
                .montoPagado(new BigDecimal("100.00"))
                .estado(Pago.EstadoPago.COMPLETADO)
                .build();
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);
        when(pagoMapper.toResponse(pagoGuardado)).thenReturn(new PagoResponse());

        PagoResponse response = pagoService.registrar(pagoRequest);

        assertThat(response).isNotNull();
        verify(pagoRepository).save(argThat(p -> p.getEstado() == Pago.EstadoPago.COMPLETADO));
    }

    @Test
    @DisplayName("Registrar pago que supera deuda pendiente lanza BusinessException")
    void registrar_montoSuperaDeuda_lanzaExcepcion() {
        pagoRequest.setMontoPagado(new BigDecimal("150.00"));
        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
        when(pagoRepository.sumMontoPagadoByIdCuota(1L)).thenReturn(new BigDecimal("50.00"));

        assertThatThrownBy(() -> pagoService.registrar(pagoRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("deuda pendiente");
    }

    @Test
    @DisplayName("No se puede pagar una cuota ya completamente pagada")
    void registrar_cuotaYaPagada_lanzaExcepcion() {
        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
        when(pagoRepository.sumMontoPagadoByIdCuota(1L)).thenReturn(new BigDecimal("100.00"));

        assertThatThrownBy(() -> pagoService.registrar(pagoRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("completamente pagada");
    }

    @Test
    @DisplayName("Pago parcial asigna estado PARCIAL")
    void registrar_pagoParcial_estadoParcial() {
        pagoRequest.setMontoPagado(new BigDecimal("50.00"));
        when(cuotaRepository.findById(1L)).thenReturn(Optional.of(cuota));
        when(pagoRepository.sumMontoPagadoByIdCuota(1L)).thenReturn(BigDecimal.ZERO);
        Pago pagoGuardado = Pago.builder()
                .idPago(2L).cuota(cuota)
                .montoPagado(new BigDecimal("50.00"))
                .estado(Pago.EstadoPago.PARCIAL)
                .build();
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);
        when(pagoMapper.toResponse(pagoGuardado)).thenReturn(new PagoResponse());

        pagoService.registrar(pagoRequest);

        verify(pagoRepository).save(argThat(p -> p.getEstado() == Pago.EstadoPago.PARCIAL));
    }
}
