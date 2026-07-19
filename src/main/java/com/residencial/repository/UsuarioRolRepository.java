package com.residencial.repository;

import com.residencial.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la tabla intermedia usuario_rol.
 */
@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuario_IdUsuario(Long idUsuario);
}
