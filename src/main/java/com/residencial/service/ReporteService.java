package com.residencial.service;

import com.residencial.dto.response.ReporteResponse;
import com.residencial.dto.response.ReporteResponse.Novedad;
import com.residencial.entity.Cuota;
import com.residencial.entity.Residencia;
import com.residencial.repository.ComunicadoRepository;
import com.residencial.repository.CuotaRepository;
import com.residencial.repository.ResidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de reporte general del sistema.
 * Analiza: deudas pendientes, casas desocupadas y comunicados vencidos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final CuotaRepository cuotaRepository;
    private final ResidenciaRepository residenciaRepository;
    private final ComunicadoRepository comunicadoRepository;

    public ReporteResponse generarReporteGeneral() {
        List<Novedad> novedades = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        // 1. Deudas pendientes
        List<Cuota> cuotasPendientes = cuotaRepository.findCuotasConDeudaPendiente();
        if (!cuotasPendientes.isEmpty()) {
            List<Map<String, Object>> datos = cuotasPendientes.stream()
                    .map(c -> Map.<String, Object>of(
                            "idCuota", c.getIdCuota(),
                            "codigoCasa", c.getResidencia().getCodigoCasa(),
                            "mes", c.getMes(),
                            "anio", c.getAnio(),
                            "valor", c.getValor()
                    ))
                    .collect(Collectors.toList());

            novedades.add(Novedad.builder()
                    .tipo("DEUDA_PENDIENTE")
                    .descripcion("Existen " + cuotasPendientes.size() + " cuota(s) con deuda pendiente")
                    .datos(datos)
                    .build());
        }

        // 2. Casas desocupadas
        List<Residencia> casasVacias = residenciaRepository.findCasasDesocupadas();
        if (!casasVacias.isEmpty()) {
            List<Map<String, Object>> datos = casasVacias.stream()
                    .map(r -> Map.<String, Object>of(
                            "idResidencia", r.getIdResidencia(),
                            "codigoCasa", r.getCodigoCasa()
                    ))
                    .collect(Collectors.toList());

            novedades.add(Novedad.builder()
                    .tipo("CASAS_DESOCUPADAS")
                    .descripcion("Existen " + casasVacias.size() + " casa(s) sin residentes asignados")
                    .datos(datos)
                    .build());
        }

        // 3. Comunicados vencidos
        long comunicadosVencidos = comunicadoRepository.findVencidos(hoy).size();
        if (comunicadosVencidos > 0) {
            novedades.add(Novedad.builder()
                    .tipo("COMUNICADOS_VENCIDOS")
                    .descripcion("Existen " + comunicadosVencidos + " comunicado(s) vencidos")
                    .datos(Map.of("total", comunicadosVencidos))
                    .build());
        }

        // Construir respuesta
        if (novedades.isEmpty()) {
            return ReporteResponse.builder()
                    .estado("OK")
                    .mensaje("Sin novedades")
                    .build();
        }

        return ReporteResponse.builder()
                .estado("ALERTA")
                .novedades(novedades)
                .build();
    }
}
