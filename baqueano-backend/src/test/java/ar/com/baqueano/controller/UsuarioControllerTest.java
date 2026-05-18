package ar.com.baqueano.controller;

import ar.com.baqueano.dto.usuario.UsuarioCreateDTO;
import ar.com.baqueano.dto.usuario.UsuarioListItemDTO;
import ar.com.baqueano.dto.usuario.UsuarioResponseDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(
        controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ar\\.com\\.baqueano\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private UsuarioService service;

    @Test
    void listar_devuelve_page_y_200() throws Exception {
        UsuarioListItemDTO item = new UsuarioListItemDTO(1L, "admin", "a@x.com", "Admin", "Sis", "ADMIN", true);
        Page<UsuarioListItemDTO> page = new PageImpl<>(List.of(item), PageRequest.of(0, 20), 1);
        when(service.listar(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("admin"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void crear_happy_devuelve_201_y_location() throws Exception {
        UsuarioCreateDTO dto = new UsuarioCreateDTO("pepe", "pepe@x.com", "secret12345",
                "Pepe", "Tester", 1L, true);
        UsuarioResponseDTO created = new UsuarioResponseDTO(
                42L, "pepe", "pepe@x.com", "Pepe", "Tester", 1L, "ADMIN",
                true, null, 0, null, null);
        when(service.crear(any(UsuarioCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.username").value("pepe"));
    }

    @Test
    void crear_dto_invalido_devuelve_400_con_fieldErrors() throws Exception {
        // password vacio, email malformado, perfilId null
        String body = """
                {"username":"pepe","email":"no-es-email","password":"",
                 "nombre":"P","apellido":"T","perfilId":null}
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void crear_username_duplicado_devuelve_409() throws Exception {
        UsuarioCreateDTO dto = new UsuarioCreateDTO("admin", "x@x.com", "secret12345",
                "X", "Y", 1L, true);
        when(service.crear(any(UsuarioCreateDTO.class)))
                .thenThrow(new OperacionInvalidaException("Username ya existe: admin"));

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("OPERACION_INVALIDA"))
                .andExpect(jsonPath("$.message").value("Username ya existe: admin"));
    }

    @Test
    void obtener_no_encontrado_devuelve_404() throws Exception {
        when(service.obtener(404L)).thenThrow(new EntityNotFoundException("Usuario no encontrado: id=404"));

        mockMvc.perform(get("/api/v1/usuarios/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void eliminar_devuelve_204() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/7"))
                .andExpect(status().isNoContent());

        verify(service).eliminar(eq(7L));
    }
}
