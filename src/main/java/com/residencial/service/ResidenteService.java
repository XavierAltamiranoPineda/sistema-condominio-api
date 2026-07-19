package com.residencial.service;

import com.residencial.dto.request.ResidenteRequest;
import com.residencial.dto.response.ResidenteResponse;
import com.residencial.entity.Residente;
import com.residencial.entity.Usuario;
import com.residencial.exception.BusinessException;
import com.residencial.exception.DuplicateResourceException;
import com.residencial.exception.ResourceNotFoundException;
import com.residencial.mapper.ResidenteMapper;
import com.residencial.repository.ResidenteRepository;
import com.residencial.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de residentes.
 * Contiene toda la lógica de negocio relacionada a residentes.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResidenteService {

    private final ResidenteRepository residenteRepository;
    private final UsuarioRepository   usuarioRepository;
    private final ResidenteMapper     residenteMapper;

    // ── CRUD (ADMINISTRADOR) ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ResidenteResponse> listarTodos() {
        return residenteRepository.findAll().stream()
                .map(residenteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResidenteResponse buscarPorId(Long id) {
        return residenteMapper.toResponse(obtenerEntidad(id));
    }

    public ResidenteResponse crear(ResidenteRequest request) {
        validarCedulaEcuatoriana(request.getCedula());
        if (residenteRepository.existsByCedula(request.getCedula())) {
            throw new DuplicateResourceException("Ya existe un residente con la cédula: " + request.getCedula());
        }
        Residente residente = Residente.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .cedula(request.getCedula())
                .telefono(request.getTelefono())
                .estado(Residente.EstadoResidente.ACTIVO)
                .build();
        return residenteMapper.toResponse(residenteRepository.save(residente));
    }

    public ResidenteResponse actualizar(Long id, ResidenteRequest request) {
        Residente residente = obtenerEntidad(id);
        if (!residente.getCedula().equals(request.getCedula())) {
            validarCedulaEcuatoriana(request.getCedula());
            if (residenteRepository.existsByCedula(request.getCedula())) {
                throw new DuplicateResourceException("Ya existe un residente con la cédula: " + request.getCedula());
            }
            residente.setCedula(request.getCedula());
        }
        residente.setNombres(request.getNombres());
        residente.setApellidos(request.getApellidos());
        residente.setTelefono(request.getTelefono());
        return residenteMapper.toResponse(residenteRepository.save(residente));
    }

    public void eliminar(Long id) {
        Residente residente = obtenerEntidad(id);
        residente.setEstado(Residente.EstadoResidente.INACTIVO);
        residenteRepository.save(residente);
    }

    // ── Endpoint /me (RESIDENTE) ───────────────────────────────────────────────

    /**
     * Retorna el perfil del residente vinculado al usuario autenticado.
     * Lanza 404 si el usuario no tiene residente asociado.
     */
    @Transactional(readOnly = true)
    public ResidenteResponse obtenerMiPerfil() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        if (usuario.getResidente() == null) {
            throw new BusinessException("El usuario autenticado no tiene un residente asociado");
        }
        return residenteMapper.toResponse(usuario.getResidente());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public Residente obtenerEntidad(Long id) {
        return residenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residente", id));
    }

    /**
     * Retorna el residente vinculado al usuario autenticado (para uso interno).
     */
    public Residente obtenerResidenteAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        if (usuario.getResidente() == null) {
            throw new BusinessException("El usuario autenticado no tiene un residente asociado");
        }
        return usuario.getResidente();
    }

    /**
     * Valida la cédula ecuatoriana usando el algoritmo de módulo 10.
     * Reglas: exactamente 10 dígitos, dígito verificador correcto.
     */
    private void validarCedulaEcuatoriana(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            throw new BusinessException("La cédula debe tener exactamente 10 dígitos numéricos");
        }
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            throw new BusinessException("Los primeros dos dígitos de la cédula no son válidos");
        }
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito  = Character.getNumericValue(cedula.charAt(i));
            int producto = digito * coeficientes[i];
            suma += (producto >= 10) ? producto - 9 : producto;
        }
        int digitoVerificador = (suma % 10 == 0) ? 0 : 10 - (suma % 10);
        if (digitoVerificador != Character.getNumericValue(cedula.charAt(9))) {
            throw new BusinessException("La cédula ecuatoriana no es válida");
        }
    }
}
