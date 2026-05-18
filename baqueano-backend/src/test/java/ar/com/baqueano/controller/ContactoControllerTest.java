package ar.com.baqueano.controller;

import ar.com.baqueano.domain.Contacto;
import ar.com.baqueano.dto.contacto.ContactoCreateDTO;
import ar.com.baqueano.dto.contacto.ContactoResponseDTO;
import ar.com.baqueano.dto.contacto.ContactoUpdateEstadoDTO;
import ar.com.baqueano.service.ContactoService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ContactoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ar\\.com\\.baqueano\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class ContactoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private ContactoService service;

    @Test
    void alta_publica_devuelve_201_y_estado_NUEVO() throws Exception {
        ContactoCreateDTO dto = new ContactoCreateDTO("Pepe", "p@x.com", "111",
                "Asunto", "Mensaje completo del cliente");
        ContactoResponseDTO out = new ContactoResponseDTO(7L, "Pepe", "p@x.com", "111",
                "Asunto", "Mensaje completo del cliente",
                Contacto.Estado.NUEVO, "127.0.0.1", null, null);
        when(service.crear(any(ContactoCreateDTO.class), any(String.class))).thenReturn(out);

        mockMvc.perform(post("/api/v1/contactos")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.estado").value("NUEVO"));
    }

    @Test
    void alta_mensaje_blank_devuelve_400() throws Exception {
        String body = """
                {"nombre":"Pepe","email":"p@x.com","mensaje":""}
                """;
        mockMvc.perform(post("/api/v1/contactos")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void actualizarEstado_a_RESPONDIDO_devuelve_200() throws Exception {
        ContactoResponseDTO out = new ContactoResponseDTO(7L, "Pepe", "p@x.com", null,
                null, "msg", Contacto.Estado.RESPONDIDO, null, null, null);
        when(service.actualizarEstado(eq(7L), eq(Contacto.Estado.RESPONDIDO))).thenReturn(out);

        String body = om.writeValueAsString(new ContactoUpdateEstadoDTO(Contacto.Estado.RESPONDIDO));
        mockMvc.perform(patch("/api/v1/contactos/7/estado")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESPONDIDO"));
    }
}
