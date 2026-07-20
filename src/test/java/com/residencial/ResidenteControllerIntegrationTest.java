package com.residencial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencial.entity.Residente;
import com.residencial.repository.ResidenteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ResidenteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResidenteRepository residenteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    public void testActivarResidente() throws Exception {
        String randomCedula = String.format("%010d", new java.util.Random().nextInt(1000000000));
        Residente residente = Residente.builder()
                .nombres("Test")
                .apellidos("User")
                .cedula(randomCedula)
                .telefono("0999999999")
                .estado(Residente.EstadoResidente.INACTIVO)
                .build();
        residente = residenteRepository.save(residente);

        String json = "{\"nombres\":\"Test\",\"apellidos\":\"User\",\"cedula\":\"" + randomCedula + "\",\"telefono\":\"0999999999\",\"estado\":\"ACTIVO\"}";

        mockMvc.perform(put("/api/residentes/" + residente.getIdResidente())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }
}
