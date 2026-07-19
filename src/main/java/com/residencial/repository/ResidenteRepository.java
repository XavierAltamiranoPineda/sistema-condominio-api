package com.residencial.repository;

import com.residencial.entity.Residente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Residente.
 */
@Repository
public interface ResidenteRepository extends JpaRepository<Residente, Long> {

    Optional<Residente> findByCedula(String cedula);

    boolean existsByCedula(String cedula);
}
