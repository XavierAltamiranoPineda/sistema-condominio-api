package com.residencial.repository;

import com.residencial.entity.Comunicado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad Comunicado.
 */
@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {

    /** Comunicados no vencidos o sin fecha de vencimiento */
    @Query("SELECT c FROM Comunicado c WHERE c.fechaVencimiento IS NULL OR c.fechaVencimiento >= :hoy")
    List<Comunicado> findVigentes(@Param("hoy") LocalDate hoy);

    /** Comunicados vencidos (para el reporte general) */
    @Query("SELECT c FROM Comunicado c WHERE c.fechaVencimiento IS NOT NULL AND c.fechaVencimiento < :hoy")
    List<Comunicado> findVencidos(@Param("hoy") LocalDate hoy);

    /**
     * Comunicados dirigidos a un residente específico via comunicado_residente.
     * Usado en GET /api/comunicados/me.
     */
    @Query("""
        SELECT cr.comunicado FROM ComunicadoResidente cr
        WHERE cr.residente.idResidente = :idResidente
        ORDER BY cr.fechaEnvio DESC
    """)
    List<Comunicado> findComunicadosByIdResidente(@Param("idResidente") Long idResidente);
}
