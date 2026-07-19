package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Comunicado o aviso emitido a los residentes.
 */
@Entity
@Table(name = "comunicados")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comunicado")
    private Long idComunicado;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "prioridad", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.prioridad == null) {
            this.prioridad = Prioridad.NORMAL;
        }
    }

    public enum Prioridad {
        ALTA, NORMAL, BAJA
    }
}
