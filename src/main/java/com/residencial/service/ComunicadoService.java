package com.residencial.service;

import com.residencial.dto.request.ComunicadoRequest;
import com.residencial.dto.response.ComunicadoResponse;
import com.residencial.entity.Comunicado;
import com.residencial.entity.ComunicadoResidente;
import com.residencial.entity.Residente;
import com.residencial.exception.ResourceNotFoundException;
import com.residencial.mapper.ComunicadoMapper;
import com.residencial.repository.ComunicadoRepository;
import com.residencial.repository.ResidenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de comunicados.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ComunicadoService {

    private final ComunicadoRepository comunicadoRepository;
    private final ResidenteRepository residenteRepository;
    private final ComunicadoMapper comunicadoMapper;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<ComunicadoResponse> listarTodos() {
        return comunicadoRepository.findAll().stream()
                .map(comunicadoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ComunicadoResponse buscarPorId(Long id) {
        return comunicadoMapper.toResponse(obtenerEntidad(id));
    }

    /**
     * Retorna los comunicados dirigidos al residente autenticado.
     * Usado en GET /api/comunicados/me.
     *
     * @param idResidente ID del residente autenticado
     */
    @Transactional(readOnly = true)
    public List<ComunicadoResponse> listarMisComunicados(Long idResidente) {
        return comunicadoRepository.findComunicadosByIdResidente(idResidente).stream()
                .map(comunicadoMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ComunicadoResponse crear(ComunicadoRequest request) {
        Comunicado.Prioridad prioridad = request.getPrioridad() != null
                ? Comunicado.Prioridad.valueOf(request.getPrioridad())
                : Comunicado.Prioridad.NORMAL;

        Comunicado comunicado = Comunicado.builder()
                .titulo(request.getTitulo())
                .mensaje(request.getMensaje())
                .prioridad(prioridad)
                .fechaVencimiento(request.getFechaVencimiento())
                .build();

        Comunicado guardado = comunicadoRepository.save(comunicado);

        List<Residente> destinatarios;
        if (request.getDestinatarios() != null && !request.getDestinatarios().isEmpty()) {
            destinatarios = residenteRepository.findAllById(request.getDestinatarios());
        } else {
            destinatarios = residenteRepository.findAll().stream()
                    .filter(r -> r.getEstado() == Residente.EstadoResidente.ACTIVO)
                    .collect(Collectors.toList());
        }

        destinatarios.forEach(residente -> {
            ComunicadoResidente envio = ComunicadoResidente.builder()
                    .comunicado(guardado)
                    .residente(residente)
                    .leido(false)
                    .build();
            entityManager.persist(envio);
        });

        return comunicadoMapper.toResponse(guardado);
    }

    public Comunicado obtenerEntidad(Long id) {
        return comunicadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comunicado", id));
    }
}
