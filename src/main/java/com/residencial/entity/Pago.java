package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registro de pago aplicado a una cuota mensual.
 * Soporta pagos parciales (estado PARCIAL) y completos (COMPLETADO).
 */
@Entity
@Table(name = "pagos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cuota", nullable = false)
    private Cuota cuota;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    /** Monto pagado: > 0 y <= 500 */
    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "estado", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @PrePersist
    protected void onCreate() {
        if (this.fechaPago == null) {
            this.fechaPago = LocalDate.now();
        }
        if (this.estado == null) {
            this.estado = EstadoPago.COMPLETADO;
        }
    }

    public enum EstadoPago {
        COMPLETADO, PARCIAL, PENDIENTE
    }
}
