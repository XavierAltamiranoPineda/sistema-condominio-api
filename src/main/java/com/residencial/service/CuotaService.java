package com.residencial.service;

import com.residencial.dto.request.CuotaRequest;
import com.residencial.dto.response.CuotaResponse;
import com.residencial.entity.Cuota;
import com.residencial.entity.Residencia;
import com.residencial.exception.BusinessException;
import com.residencial.exception.DuplicateResourceException;
import com.residencial.exception.ResourceNotFoundException;
import com.residencial.mapper.CuotaMapper;
import com.residencial.repository.CuotaRepository;
import com.residencial.repository.PagoRepository;
import com.residencial.repository.ResidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de cuotas mensuales.
 * Reglas: no duplicar cuota por mes/año, valor máximo $500.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CuotaService {

    private final CuotaRepository cuotaRepository;
    private final ResidenciaRepository residenciaRepository;
    private final PagoRepository pagoRepository;
    private final CuotaMapper cuotaMapper;

    @Transactional(readOnly = true)
    public List<CuotaResponse> listarTodas() {
        return cuotaRepository.findAll().stream()
                .map(c -> cuotaMapper.toResponse(c, pagoRepository.sumMontoPagadoByIdCuota(c.getIdCuota())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CuotaResponse> listarPorResidencia(Long idResidencia) {
        return cuotaRepository.findByResidencia_IdResidencia(idResidencia).stream()
                .map(c -> cuotaMapper.toResponse(c, pagoRepository.sumMontoPagadoByIdCuota(c.getIdCuota())))
                .collect(Collectors.toList());
    }

    /**
     * Retorna las cuotas de las residencias donde vive el residente autenticado.
     * Usado en GET /api/cuotas/me.
     *
     * @param idResidente ID del residente autenticado
     */
    @Transactional(readOnly = true)
    public List<CuotaResponse> listarMisCuotas(Long idResidente) {
        return cuotaRepository.findCuotasByIdResidente(idResidente).stream()
                .map(c -> cuotaMapper.toResponse(c, pagoRepository.sumMontoPagadoByIdCuota(c.getIdCuota())))
                .collect(Collectors.toList());
    }

    public CuotaResponse crear(CuotaRequest request) {
        Residencia residencia = residenciaRepository.findById(request.getIdResidencia())
                .orElseThrow(() -> new ResourceNotFoundException("Residencia", request.getIdResidencia()));

        // Verificar que no existe cuota para ese mes/año en la misma residencia
        if (cuotaRepository.existsByResidencia_IdResidenciaAndMesAndAnio(
                request.getIdResidencia(), request.getMes(), request.getAnio())) {
            throw new DuplicateResourceException(
                    "Ya existe una cuota para la residencia en " + request.getMes() + "/" + request.getAnio());
        }

        Cuota cuota = Cuota.builder()
                .residencia(residencia)
                .mes(request.getMes())
                .anio(request.getAnio())
                .valor(request.getValor())
                .build();

        Cuota guardada = cuotaRepository.save(cuota);
        return cuotaMapper.toResponse(guardada, BigDecimal.ZERO);
    }

    public Cuota obtenerEntidad(Long id) {
        return cuotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota", id));
    }
}
