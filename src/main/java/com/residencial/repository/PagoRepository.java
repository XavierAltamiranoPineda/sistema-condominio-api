package com.residencial.repository;

import com.residencial.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio JPA para la entidad Pago.
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByCuota_IdCuota(Long idCuota);

    /** Suma de montos pagados para una cuota (para validar deuda residual) */
    @Query("SELECT COALESCE(SUM(p.montoPagado), 0) FROM Pago p WHERE p.cuota.idCuota = :idCuota")
    BigDecimal sumMontoPagadoByIdCuota(@Param("idCuota") Long idCuota);

    /** Pagos relacionados con residencias de un residente */
    @Query("""
        SELECT p FROM Pago p
        JOIN p.cuota c
        JOIN c.residencia res
        JOIN ResidenteResidencia rr ON rr.residencia = res
        WHERE rr.residente.idResidente = :idResidente
    """)
    List<Pago> findByIdResidente(@Param("idResidente") Long idResidente);
}
