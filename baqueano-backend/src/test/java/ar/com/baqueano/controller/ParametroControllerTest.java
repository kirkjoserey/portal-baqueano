package ar.com.baqueano.controller;

import ar.com.baqueano.domain.Parametro;
import ar.com.baqueano.dto.parametro.ParametroResponseDTO;
import ar.com.baqueano.service.ParametroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ParametroController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ar\\.com\\.baqueano\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class ParametroControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private ParametroService service;

    @Test
    void obtenerPorClave_happy_devuelve_dto() throws Exception {
        when(service.obtenerPorClave("app.nombre"))
                .thenReturn(new ParametroResponseDTO(1L, "app.nombre", "Baqueano", "desc",
                        Parametro.TipoDato.STRING, true, null, null));

        mockMvc.perform(get("/api/v1/parametros/clave/app.nombre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("Baqueano"))
                .andExpect(jsonPath("$.tipoDato").value("STRING"));
    }

    @Test
    void obtenerPorClave_inexistente_devuelve_404() throws Exception {
        when(service.obtenerPorClave("no.existe"))
                .thenThrow(new EntityNotFoundException("Parametro no encontrado: clave=no.existe"));

        mockMvc.perform(get("/api/v1/parametros/clave/no.existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
