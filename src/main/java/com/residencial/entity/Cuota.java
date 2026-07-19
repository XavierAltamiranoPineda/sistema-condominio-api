package com.residencial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Cuota mensual generada para una residencia.
 * Una residencia no puede tener dos cuotas del mismo mes y año.
 */
@Entity
@Table(name = "cuotas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_residencia", "mes", "anio"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Long idCuota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_residencia", nullable = false)
    private Residencia residencia;

    /** Número de mes: 1 (enero) – 12 (diciembre) */
    @Column(name = "mes", nullable = false)
    private Short mes;

    @Column(name = "anio", nullable = false)
    private Short anio;

    /** Valor máximo 500 dólares */
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}
