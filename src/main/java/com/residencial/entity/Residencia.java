package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una residencia (casa) del condominio.
 *
 * CORRECCIONES:
 *  - propietario: NOT NULL (obligatorio)
 *  - estado: OCUPADA | DESOCUPADA (reemplaza ACTIVO/INACTIVO)
 *  - cuotaMensual: > 0 y <= 500
 */
@Entity
@Table(name = "residencias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Residencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_residencia")
    private Long idResidencia;

    /**
     * Propietario de la residencia — obligatorio.
     * No se puede eliminar un residente que sea propietario (ON DELETE RESTRICT en BD).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_propietario", nullable = false)
    private Residente propietario;

    @Column(name = "codigo_casa", nullable = false, unique = true, length = 20)
    private String codigoCasa;

    /** Cuota mensual en dólares. Rango: > 0 y <= 500 */
    @Column(name = "cuota_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal cuotaMensual;

    @Column(name = "estado", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private EstadoResidencia estado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoResidencia.DESOCUPADA;
        }
    }

    public enum EstadoResidencia {
        OCUPADA, DESOCUPADA
    }
}
