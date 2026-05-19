package ar.com.baqueano;

import ar.com.baqueano.dto.auth.LoginRequestDTO;
import ar.com.baqueano.dto.auth.LoginResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion end-to-end: arranca Spring Boot completo en puerto
 * aleatorio contra la BD baqueano_test, hace login real con admin/admin123
 * (el AdminPasswordRunner regenera el hash desde el placeholder en V2),
 * y consume un endpoint protegido con el JWT obtenido.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void login_admin_y_acceso_protegido_devuelve_menu_dinamico() {
        // 1. Login publico
        LoginRequestDTO body = new LoginRequestDTO("admin", "admin123");
        ResponseEntity<LoginResponseDTO> loginRes = rest.postForEntity(
                "http://localhost:" + port + "/baqueano/api/v1/auth/login",
                body,
                LoginResponseDTO.class);

        assertThat(loginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponseDTO loginBody = loginRes.getBody();
        assertThat(loginBody).isNotNull();
        assertThat(loginBody.accessToken()).isNotBlank();
        assertThat(loginBody.refreshToken()).isNotBlank();
        assertThat(loginBody.perfilNombre()).isEqualTo("ADMIN");

        // 2. Endpoint protegido sin token -> 401
        ResponseEntity<String> sinToken = rest.getForEntity(
                "http://localhost:" + port + "/baqueano/api/v1/menu/mio",
                String.class);
        assertThat(sinToken.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // 3. Endpoint protegido con token -> 200 con el arbol del menu
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginBody.accessToken());
        ResponseEntity<String> conToken = rest.exchange(
                "http://localhost:" + port + "/baqueano/api/v1/menu/mio",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(conToken.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(conToken.getBody())
                .contains("MENU PRINCIPAL")
                .contains("/dashboard")
                .contains("puedeCrear");
    }

    @Test
    void login_password_incorrecto_devuelve_401_uniforme() {
        LoginRequestDTO body = new LoginRequestDTO("admin", "WRONG");
        ResponseEntity<String> res = rest.postForEntity(
                "http://localhost:" + port + "/baqueano/api/v1/auth/login",
                body,
                String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody()).contains("CREDENCIALES_INVALIDAS");
    }

    @Test
    void actuator_health_es_publico() {
        ResponseEntity<String> res = rest.getForEntity(
                "http://localhost:" + port + "/baqueano/actuator/health",
                String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("\"status\":\"UP\"");
    }
}
