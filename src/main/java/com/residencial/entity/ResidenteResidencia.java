package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Tabla intermedia que relaciona residentes con sus residencias (relación N:M).
 */
@Entity
@Table(name = "residente_residencia",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_residente", "id_residencia"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ResidenteResidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Long idAsignacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_residente", nullable = false)
    private Residente residente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_residencia", nullable = false)
    private Residencia residencia;

    @Column(name = "parentesco", length = 50)
    private String parentesco;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaAsignacion == null) {
            this.fechaAsignacion = LocalDate.now();
        }
    }
}
