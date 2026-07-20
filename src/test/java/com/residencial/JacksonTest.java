package com.residencial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.residencial.dto.request.ResidenteRequest;
import com.residencial.entity.Residente.EstadoResidente;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JacksonTest {

    @Test
    public void testDeserialization() throws Exception {
        String json = "{\"nombres\":\"Test\",\"apellidos\":\"User\",\"cedula\":\"1700000001\",\"telefono\":\"0999999999\",\"estado\":\"ACTIVO\"}";
        ObjectMapper mapper = new ObjectMapper();
        ResidenteRequest request = mapper.readValue(json, ResidenteRequest.class);
        
        assertThat(request.getEstado()).isEqualTo(EstadoResidente.ACTIVO);
    }
}
