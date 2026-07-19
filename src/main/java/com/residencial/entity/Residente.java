package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa a un residente del condominio.
 */
@Entity
@Table(name = "residentes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Residente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_residente")
    private Long idResidente;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /** Cédula ecuatoriana de 10 dígitos, única */
    @Column(name = "cedula", nullable = false, unique = true, length = 10)
    private String cedula;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "estado", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private EstadoResidente estado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoResidente.ACTIVO;
        }
    }

    public enum EstadoResidente {
        ACTIVO, INACTIVO
    }
}
