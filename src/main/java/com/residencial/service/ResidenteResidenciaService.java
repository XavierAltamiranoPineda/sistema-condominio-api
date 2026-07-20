package com.residencial.service;

import com.residencial.dto.request.AsignacionRequest;
import com.residencial.entity.Residencia;
import com.residencial.entity.Residente;
import com.residencial.entity.ResidenteResidencia;
import com.residencial.exception.BusinessException;
import com.residencial.exception.DuplicateResourceException;
import com.residencial.exception.ResourceNotFoundException;
import com.residencial.repository.ResidenciaRepository;
import com.residencial.repository.ResidenteRepository;
import com.residencial.repository.ResidenteResidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ResidenteResidenciaService {

    private final ResidenteResidenciaRepository residenteResidenciaRepository;
    private final ResidenteRepository residenteRepository;
    private final ResidenciaRepository residenciaRepository;

    private static final int MAX_RESIDENTES_POR_RESIDENCIA = 10;

    public void asignar(AsignacionRequest request) {
        // 1. El residente existe
        Residente residente = residenteRepository.findById(request.getIdResidente())
                .orElseThrow(() -> new ResourceNotFoundException("Residente", request.getIdResidente()));

        // 2. La residencia existe
        Residencia residencia = residenciaRepository.findById(request.getIdResidencia())
                .orElseThrow(() -> new ResourceNotFoundException("Residencia", request.getIdResidencia()));

        // 3. El residente está ACTIVO
        if (residente.getEstado() != Residente.EstadoResidente.ACTIVO) {
            throw new BusinessException("No se puede asignar un residente inactivo a una residencia");
        }

        // 4. La residencia permite más residentes
        long count = residenteResidenciaRepository.countByResidencia_IdResidencia(residencia.getIdResidencia());
        if (count >= MAX_RESIDENTES_POR_RESIDENCIA) {
            throw new BusinessException("La residencia ya tiene el máximo de " + MAX_RESIDENTES_POR_RESIDENCIA + " residentes permitidos");
        }

        // 5. No existe asignación duplicada
        if (residenteResidenciaRepository.existsByResidente_IdResidenteAndResidencia_IdResidencia(residente.getIdResidente(), residencia.getIdResidencia())) {
            throw new DuplicateResourceException("El residente ya se encuentra asignado a esta residencia");
        }

        // Guardar
        ResidenteResidencia asignacion = ResidenteResidencia.builder()
                .residente(residente)
                .residencia(residencia)
                .parentesco(request.getParentesco())
                .fechaAsignacion(LocalDate.now())
                .build();

        residenteResidenciaRepository.save(asignacion);
    }
}
