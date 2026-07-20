package com.residencial.service;

import com.residencial.dto.request.ResidenciaRequest;
import com.residencial.dto.response.ResidenciaResponse;
import com.residencial.entity.Residente;
import com.residencial.entity.Residencia;
import com.residencial.exception.BusinessException;
import com.residencial.exception.DuplicateResourceException;
import com.residencial.exception.ResourceNotFoundException;
import com.residencial.mapper.ResidenciaMapper;
import com.residencial.repository.ResidenciaRepository;
import com.residencial.repository.ResidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de residencias.
 *
 * Reglas de negocio:
 *  - Propietario obligatorio (NOT NULL).
 *  - Máx. 10 casas por propietario (validado en Service y también en BD via trigger).
 *  - Estado: OCUPADA | DESOCUPADA.
 *  - Cuota mensual: > 0 y <= 500.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResidenciaService {

    private static final int MAX_CASAS_POR_PROPIETARIO = 10;

    private final ResidenciaRepository residenciaRepository;
    private final ResidenteRepository residenteRepository;
    private final ResidenciaMapper residenciaMapper;

    @Transactional(readOnly = true)
    public List<ResidenciaResponse> listarTodas() {
        return residenciaRepository.findAll().stream()
                .map(residenciaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna las residencias donde habita el residente.
     * Usado en GET /api/residencias/me.
     */
    @Transactional(readOnly = true)
    public List<ResidenciaResponse> listarMisResidencias(Long idResidente) {
        return residenciaRepository.findResidenciasByIdResidente(idResidente).stream()
                .map(residenciaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResidenciaResponse buscarPorId(Long id) {
        return residenciaMapper.toResponse(obtenerEntidad(id));
    }

    public ResidenciaResponse crear(ResidenciaRequest request) {
        // Verificar código único
        if (residenciaRepository.existsByCodigoCasa(request.getCodigoCasa())) {
            throw new DuplicateResourceException(
                    "Ya existe una residencia con el código: " + request.getCodigoCasa());
        }

        // Propietario es obligatorio
        Residente propietario = residenteRepository.findById(request.getIdPropietario())
                .orElseThrow(() -> new ResourceNotFoundException("Residente", request.getIdPropietario()));

        if (propietario.getEstado() == Residente.EstadoResidente.INACTIVO) {
            throw new BusinessException("No se puede asignar un residente inactivo como propietario de una residencia");
        }

        // Validar máximo de casas por propietario (doble capa: Service + trigger BD)
        long totalCasas = residenciaRepository.countByPropietario_IdResidente(propietario.getIdResidente());
        if (totalCasas >= MAX_CASAS_POR_PROPIETARIO) {
            throw new BusinessException("El propietario ya tiene el máximo de "
                    + MAX_CASAS_POR_PROPIETARIO + " casas permitidas");
        }

        Residencia residencia = Residencia.builder()
                .propietario(propietario)
                .codigoCasa(request.getCodigoCasa())
                .cuotaMensual(request.getCuotaMensual())
                .estado(Residencia.EstadoResidencia.DESOCUPADA)
                .build();

        return residenciaMapper.toResponse(residenciaRepository.save(residencia));
    }

    public ResidenciaResponse actualizar(Long id, ResidenciaRequest request) {
        Residencia residencia = obtenerEntidad(id);

        // Si cambia el código, verificar unicidad
        if (!residencia.getCodigoCasa().equals(request.getCodigoCasa())) {
            if (residenciaRepository.existsByCodigoCasa(request.getCodigoCasa())) {
                throw new DuplicateResourceException(
                        "Ya existe una residencia con el código: " + request.getCodigoCasa());
            }
            residencia.setCodigoCasa(request.getCodigoCasa());
        }

        // Actualizar propietario — siempre obligatorio
        Residente nuevoPropietario = residenteRepository.findById(request.getIdPropietario())
                .orElseThrow(() -> new ResourceNotFoundException("Residente", request.getIdPropietario()));

        if (nuevoPropietario.getEstado() == Residente.EstadoResidente.INACTIVO) {
            throw new BusinessException("No se puede asignar un residente inactivo como propietario de una residencia");
        }

        // Verificar límite solo si el propietario cambia
        boolean propietarioCambia = !residencia.getPropietario().getIdResidente()
                .equals(request.getIdPropietario());

        if (propietarioCambia) {
            long totalCasas = residenciaRepository.countByPropietario_IdResidente(
                    nuevoPropietario.getIdResidente());
            if (totalCasas >= MAX_CASAS_POR_PROPIETARIO) {
                throw new BusinessException("El propietario ya tiene el máximo de "
                        + MAX_CASAS_POR_PROPIETARIO + " casas permitidas");
            }
        }

        residencia.setPropietario(nuevoPropietario);
        residencia.setCuotaMensual(request.getCuotaMensual());

        // Actualizar estado si se envía en el request
        if (request.getEstado() != null) {
            residencia.setEstado(request.getEstado());
        }

        return residenciaMapper.toResponse(residenciaRepository.save(residencia));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    public Residencia obtenerEntidad(Long id) {
        return residenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residencia", id));
    }
}
