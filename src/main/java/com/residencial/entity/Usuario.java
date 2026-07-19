package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Usuario del sistema. Puede estar vinculado a un residente.
 */
@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    /** Residente asociado (puede ser null para usuarios administradores sin residencia) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_residente")
    private Residente residente;

    @Column(name = "usuario", nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "estado", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoUsuario.ACTIVO;
        }
    }

    public enum EstadoUsuario {
        ACTIVO, INACTIVO
    }
}
