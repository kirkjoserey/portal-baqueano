package ar.com.baqueano.controller;

import ar.com.baqueano.dto.dashboard.DashboardResumenDTO;
import ar.com.baqueano.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = DashboardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ar\\.com\\.baqueano\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DashboardService service;

    @Test
    void resumen_devuelve_totales() throws Exception {
        when(service.obtenerResumen()).thenReturn(
                new DashboardResumenDTO(7L, 3L, 15L, 4L, List.of()));

        mockMvc.perform(get("/api/v1/dashboard/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(7))
                .andExpect(jsonPath("$.totalPerfiles").value(3))
                .andExpect(jsonPath("$.totalContactos").value(15))
                .andExpect(jsonPath("$.contactosNuevos").value(4));
    }
}
