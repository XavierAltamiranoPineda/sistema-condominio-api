package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Tabla intermedia que registra el envío y lectura de comunicados por residente.
 */
@Entity
@Table(name = "comunicado_residente",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_comunicado", "id_residente"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ComunicadoResidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Long idEnvio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_comunicado", nullable = false)
    private Comunicado comunicado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_residente", nullable = false)
    private Residente residente;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "leido", nullable = false)
    private Boolean leido;

    @PrePersist
    protected void onCreate() {
        if (this.fechaEnvio == null) {
            this.fechaEnvio = LocalDateTime.now();
        }
        if (this.leido == null) {
            this.leido = false;
        }
    }
}
