package ar.com.baqueano.controller;

import ar.com.baqueano.dto.perfil.PerfilCreateDTO;
import ar.com.baqueano.dto.perfil.PerfilResponseDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.service.PerfilService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PerfilController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ar\\.com\\.baqueano\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private PerfilService service;

    @Test
    void crear_happy_devuelve_201() throws Exception {
        PerfilCreateDTO dto = new PerfilCreateDTO("NUEVO", "descripcion", true);
        when(service.crear(any(PerfilCreateDTO.class)))
                .thenReturn(new PerfilResponseDTO(10L, "NUEVO", "descripcion", true, null, null));

        mockMvc.perform(post("/api/v1/perfiles")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("NUEVO"));
    }

    @Test
    void crear_nombre_blank_devuelve_400_validation() throws Exception {
        String body = """
                {"nombre":"","descripcion":"x","activo":true}
                """;
        mockMvc.perform(post("/api/v1/perfiles")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void eliminar_perfil_con_usuarios_devuelve_409() throws Exception {
        doThrow(new OperacionInvalidaException("No se puede eliminar el perfil porque tiene usuarios asociados"))
                .when(service).eliminar(1L);

        mockMvc.perform(delete("/api/v1/perfiles/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("OPERACION_INVALIDA"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("usuarios asociados")));
    }
}
