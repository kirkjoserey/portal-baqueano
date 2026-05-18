package ar.com.baqueano.controller;

import ar.com.baqueano.dto.auth.LoginRequestDTO;
import ar.com.baqueano.dto.auth.LoginResponseDTO;
import ar.com.baqueano.dto.auth.RefreshRequestDTO;
import ar.com.baqueano.dto.auth.TokenResponseDTO;
import ar.com.baqueano.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ar\\.com\\.baqueano\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private AuthService service;

    @Test
    void login_happy_devuelve_200_y_tokens() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("admin", "admin123");
        when(service.login(any(LoginRequestDTO.class), any(String.class)))
                .thenReturn(new LoginResponseDTO("jwt.access", "raw.refresh", "Bearer", 1800L,
                        1L, "admin", 1L, "ADMIN"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt.access"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.perfilNombre").value("ADMIN"));
    }

    @Test
    void login_credenciales_invalidas_devuelve_401_con_code_CREDENCIALES_INVALIDAS() throws Exception {
        when(service.login(any(LoginRequestDTO.class), any(String.class)))
                .thenThrow(new BadCredentialsException("Credenciales invalidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(new LoginRequestDTO("admin", "wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("CREDENCIALES_INVALIDAS"));
    }

    @Test
    void login_usuario_bloqueado_devuelve_401_con_code_USUARIO_BLOQUEADO() throws Exception {
        when(service.login(any(LoginRequestDTO.class), any(String.class)))
                .thenThrow(new LockedException("Usuario bloqueado por intentos fallidos"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(new LoginRequestDTO("admin", "x"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("USUARIO_BLOQUEADO"));
    }

    @Test
    void login_username_blank_devuelve_400_validation() throws Exception {
        String body = """
                {"username":"","password":"admin123"}
                """;
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void logout_devuelve_204_y_llama_al_service() throws Exception {
        String body = om.writeValueAsString(new RefreshRequestDTO("raw-token"));
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        verify(service).logout(eq("raw-token"));
    }

    @Test
    void refresh_happy_devuelve_nuevo_access() throws Exception {
        when(service.refresh(eq("raw-refresh")))
                .thenReturn(new TokenResponseDTO("nuevo.access", "Bearer", 1800L));

        String body = om.writeValueAsString(new RefreshRequestDTO("raw-refresh"));
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("nuevo.access"));
    }
}
