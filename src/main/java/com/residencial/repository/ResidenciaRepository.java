package com.residencial.repository;

import com.residencial.entity.Residencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Residencia.
 */
@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, Long> {

    Optional<Residencia> findByCodigoCasa(String codigoCasa);

    boolean existsByCodigoCasa(String codigoCasa);

    /** Cuenta cuántas casas tiene asignadas un propietario */
    long countByPropietario_IdResidente(Long idPropietario);

    /**
     * Residencias con estado DESOCUPADA (sin residentes asignados según el estado de la entidad).
     * Usada en el reporte general.
     */
    @Query("""
        SELECT r FROM Residencia r
        WHERE r.estado = com.residencial.entity.Residencia.EstadoResidencia.DESOCUPADA
    """)
    List<Residencia> findCasasDesocupadas();

    /**
     * Residencias en las que habita un residente.
     * Usado en el endpoint GET /api/residencias/me.
     */
    @Query("""
        SELECT r FROM Residencia r
        JOIN ResidenteResidencia rr ON rr.residencia = r
        WHERE rr.residente.idResidente = :idResidente
    """)
    List<Residencia> findResidenciasByIdResidente(@org.springframework.data.repository.query.Param("idResidente") Long idResidente);
}
