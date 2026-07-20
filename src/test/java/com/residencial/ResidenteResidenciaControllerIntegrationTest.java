package com.residencial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencial.dto.request.AsignacionRequest;
import com.residencial.entity.Residencia;
import com.residencial.entity.Residente;
import com.residencial.entity.ResidenteResidencia;
import com.residencial.repository.ResidenciaRepository;
import com.residencial.repository.ResidenteRepository;
import com.residencial.repository.ResidenteResidenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ResidenteResidenciaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResidenteRepository residenteRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private ResidenteResidenciaRepository asignacionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Residente residenteActivo;
    private Residente residenteInactivo;
    private Residencia residencia;
    private Residente propietario;

    @BeforeEach
    public void setup() {
        String cedula1 = String.format("%010d", new Random().nextInt(1000000000));
        String cedula2 = String.format("%010d", new Random().nextInt(1000000000));
        String cedulaProp = String.format("%010d", new Random().nextInt(1000000000));

        propietario = residenteRepository.save(Residente.builder()
                .nombres("Propietario")
                .apellidos("Test")
                .cedula(cedulaProp)
                .estado(Residente.EstadoResidente.ACTIVO)
                .build());

        residenteActivo = residenteRepository.save(Residente.builder()
                .nombres("Activo")
                .apellidos("User")
                .cedula(cedula1)
                .estado(Residente.EstadoResidente.ACTIVO)
                .build());

        residenteInactivo = residenteRepository.save(Residente.builder()
                .nombres("Inactivo")
                .apellidos("User")
                .cedula(cedula2)
                .estado(Residente.EstadoResidente.INACTIVO)
                .build());

        residencia = residenciaRepository.save(Residencia.builder()
                .propietario(propietario)
                .codigoCasa("CASA-" + new Random().nextInt(10000))
                .cuotaMensual(new BigDecimal("100.00"))
                .estado(Residencia.EstadoResidencia.DESOCUPADA)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void caso1_ResidenteActivo_Exitoso() throws Exception {
        AsignacionRequest req = new AsignacionRequest();
        req.setIdResidente(residenteActivo.getIdResidente());
        req.setIdResidencia(residencia.getIdResidencia());

        mockMvc.perform(post("/api/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void caso2_ResidenteInactivo_Falla() throws Exception {
        AsignacionRequest req = new AsignacionRequest();
        req.setIdResidente(residenteInactivo.getIdResidente());
        req.setIdResidencia(residencia.getIdResidencia());

        mockMvc.perform(post("/api/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se puede asignar un residente inactivo a una residencia"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void caso3_ResidenteYaAsignado_Falla() throws Exception {
        asignacionRepository.save(ResidenteResidencia.builder()
                .residente(residenteActivo)
                .residencia(residencia)
                .fechaAsignacion(LocalDate.now())
                .build());

        AsignacionRequest req = new AsignacionRequest();
        req.setIdResidente(residenteActivo.getIdResidente());
        req.setIdResidencia(residencia.getIdResidencia());

        mockMvc.perform(post("/api/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El residente ya se encuentra asignado a esta residencia"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void caso4_ResidenciaCon10Residentes_Falla() throws Exception {
        for (int i = 0; i < 10; i++) {
            Residente r = residenteRepository.save(Residente.builder()
                    .nombres("Res " + i)
                    .apellidos("Test")
                    .cedula(String.format("%010d", new Random().nextInt(1000000000)))
                    .estado(Residente.EstadoResidente.ACTIVO)
                    .build());
            asignacionRepository.save(ResidenteResidencia.builder()
                    .residente(r)
                    .residencia(residencia)
                    .fechaAsignacion(LocalDate.now())
                    .build());
        }

        AsignacionRequest req = new AsignacionRequest();
        req.setIdResidente(residenteActivo.getIdResidente());
        req.setIdResidencia(residencia.getIdResidencia());

        mockMvc.perform(post("/api/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La residencia ya tiene el máximo de 10 residentes permitidos"));
    }
}
