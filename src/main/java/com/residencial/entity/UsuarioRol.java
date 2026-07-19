package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla intermedia que asigna roles a usuarios.
 */
@Entity
@Table(name = "usuario_rol",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_rol"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_rol")
    private Long idUsuarioRol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}
