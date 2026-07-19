package com.residencial.repository;

import com.residencial.entity.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Cuota.
 */
@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    List<Cuota> findByResidencia_IdResidencia(Long idResidencia);

    boolean existsByResidencia_IdResidenciaAndMesAndAnio(Long idResidencia, Short mes, Short anio);

    Optional<Cuota> findByResidencia_IdResidenciaAndMesAndAnio(Long idResidencia, Short mes, Short anio);

    /**
     * Cuotas con deuda pendiente: suma de pagos < valor de la cuota.
     * Útil para el reporte general.
     */
    @Query("""
        SELECT c FROM Cuota c
        WHERE (
            SELECT COALESCE(SUM(p.montoPagado), 0)
            FROM Pago p WHERE p.cuota = c
        ) < c.valor
    """)
    List<Cuota> findCuotasConDeudaPendiente();

    /**
     * Cuotas pertenecientes a las residencias donde vive un residente.
     * Usado en GET /api/cuotas/me.
     */
    @Query("""
        SELECT c FROM Cuota c
        JOIN ResidenteResidencia rr ON rr.residencia = c.residencia
        WHERE rr.residente.idResidente = :idResidente
    """)
    List<Cuota> findCuotasByIdResidente(@Param("idResidente") Long idResidente);
}
