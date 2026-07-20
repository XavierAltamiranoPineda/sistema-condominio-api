package com.residencial;

import com.residencial.dto.request.ResidenteRequest;
import com.residencial.dto.response.ResidenteResponse;
import com.residencial.entity.Residente;
import com.residencial.repository.ResidenteRepository;
import com.residencial.service.ResidenteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ResidenteUpdateStateTest {

    @Autowired
    private ResidenteService residenteService;

    @Autowired
    private ResidenteRepository residenteRepository;

    @Test
    public void testActualizarEstado() {
        // Create a resident directly to bypass any validation in create
        String randomCedula = java.util.UUID.randomUUID().toString().substring(0, 10);
        Residente residente = Residente.builder()
                .nombres("Test")
                .apellidos("User")
                .cedula(randomCedula) // Any string
                .telefono("0999999999")
                .estado(Residente.EstadoResidente.INACTIVO)
                .build();
        residente = residenteRepository.save(residente);

        // Update it
        ResidenteRequest updateReq = new ResidenteRequest();
        updateReq.setNombres("Test");
        updateReq.setApellidos("User");
        updateReq.setCedula(randomCedula);
        updateReq.setTelefono("0999999999");
        updateReq.setEstado(Residente.EstadoResidente.ACTIVO);

        ResidenteResponse response = residenteService.actualizar(residente.getIdResidente(), updateReq);

        assertThat(response.getEstado()).isEqualTo("ACTIVO");
        
        // Verify in DB
        Residente inDb = residenteRepository.findById(residente.getIdResidente()).get();
        assertThat(inDb.getEstado()).isEqualTo(Residente.EstadoResidente.ACTIVO);
    }
}
