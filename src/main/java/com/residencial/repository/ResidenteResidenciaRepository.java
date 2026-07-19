package com.residencial.repository;

import com.residencial.entity.ResidenteResidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la tabla intermedia residente_residencia.
 */
@Repository
public interface ResidenteResidenciaRepository extends JpaRepository<ResidenteResidencia, Long> {

    List<ResidenteResidencia> findByResidencia_IdResidencia(Long idResidencia);

    List<ResidenteResidencia> findByResidente_IdResidente(Long idResidente);

    /** Cuenta residentes actuales en una residencia */
    long countByResidencia_IdResidencia(Long idResidencia);

    boolean existsByResidente_IdResidenteAndResidencia_IdResidencia(Long idResidente, Long idResidencia);
}
