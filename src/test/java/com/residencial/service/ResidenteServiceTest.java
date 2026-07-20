package com.residencial.service;

import com.residencial.dto.request.ResidenteRequest;
import com.residencial.dto.response.ResidenteResponse;
import com.residencial.entity.Residente;
import com.residencial.exception.BusinessException;
import com.residencial.exception.DuplicateResourceException;
import com.residencial.mapper.ResidenteMapper;
import com.residencial.repository.ResidenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResidenteService - Pruebas unitarias")
class ResidenteServiceTest {

    @Mock private ResidenteRepository residenteRepository;
    @Mock private ResidenteMapper residenteMapper;

    @InjectMocks private ResidenteService residenteService;

    private Residente residente;
    private ResidenteResponse residenteResponse;

    @BeforeEach
    void setUp() {
        residente = Residente.builder()
                .idResidente(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .cedula("0912345675")  // Cédula válida del Ecuador
                .telefono("0991234567")
                .estado(Residente.EstadoResidente.ACTIVO)
                .createdAt(LocalDateTime.now())
                .build();

        residenteResponse = ResidenteResponse.builder()
                .idResidente(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .cedula("0912345675")
                .estado("ACTIVO")
                .build();
    }

    @Test
    @DisplayName("Listar todos los residentes retorna lista correcta")
    void listarTodos_retornaLista() {
        when(residenteRepository.findAll()).thenReturn(List.of(residente));
        when(residenteMapper.toResponse(residente)).thenReturn(residenteResponse);

        List<ResidenteResponse> resultado = residenteService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCedula()).isEqualTo("0912345675");
    }

    @Test
    @DisplayName("Crear residente con cédula duplicada lanza DuplicateResourceException")
    void crear_cedulaDuplicada_lanzaExcepcion() {
        ResidenteRequest request = new ResidenteRequest();
        request.setNombres("Ana");
        request.setApellidos("García");
        request.setCedula("0912345675");

        when(residenteRepository.existsByCedula("0912345675")).thenReturn(true);

        assertThatThrownBy(() -> residenteService.crear(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(residenteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear residente con cédula inválida lanza BusinessException")
    void crear_cedulaInvalida_lanzaExcepcion() {
        ResidenteRequest request = new ResidenteRequest();
        request.setNombres("Pedro");
        request.setApellidos("López");
        request.setCedula("1234567890"); // Dígito verificador incorrecto

        assertThatThrownBy(() -> residenteService.crear(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cédula");
    }

    @Test
    @DisplayName("Eliminar residente realiza soft delete (estado INACTIVO)")
    void eliminar_marcaComoInactivo() {
        when(residenteRepository.findById(1L)).thenReturn(Optional.of(residente));
        when(residenteRepository.save(any(Residente.class))).thenReturn(residente);

        residenteService.eliminar(1L);

        assertThat(residente.getEstado()).isEqualTo(Residente.EstadoResidente.INACTIVO);
        verify(residenteRepository).save(residente);
    }
}
