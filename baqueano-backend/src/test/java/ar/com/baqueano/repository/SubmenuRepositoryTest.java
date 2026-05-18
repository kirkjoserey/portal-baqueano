package ar.com.baqueano.repository;

import ar.com.baqueano.config.JpaConfig;
import ar.com.baqueano.domain.Submenu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
class SubmenuRepositoryTest {

    private static final long ADMIN_PERFIL_ID    = 1L;
    private static final long GESTOR_PERFIL_ID   = 2L;
    private static final long CONSULTA_PERFIL_ID = 3L;

    @Autowired
    private SubmenuRepository repo;

    @Test
    void findMenuPorPerfil_admin_ve_los_5_submenus_en_orden() {
        List<Submenu> menus = repo.findMenuPorPerfil(ADMIN_PERFIL_ID);

        assertThat(menus).hasSize(5);
        assertThat(menus).extracting(Submenu::getRuta)
                .containsExactly("/dashboard", "/parametros", "/usuarios", "/perfiles", "/contactos");
    }

    @Test
    void findMenuPorPerfil_gestor_no_ve_usuarios_ni_perfiles() {
        List<Submenu> menus = repo.findMenuPorPerfil(GESTOR_PERFIL_ID);

        assertThat(menus).hasSize(3);
        assertThat(menus).extracting(Submenu::getRuta)
                .doesNotContain("/usuarios", "/perfiles")
                .containsExactly("/dashboard", "/parametros", "/contactos");
    }

    @Test
    void findMenuPorPerfil_consulta_no_ve_parametros() {
        List<Submenu> menus = repo.findMenuPorPerfil(CONSULTA_PERFIL_ID);

        assertThat(menus).hasSize(4);
        assertThat(menus).extracting(Submenu::getRuta)
                .doesNotContain("/parametros")
                .containsExactly("/dashboard", "/usuarios", "/perfiles", "/contactos");
    }

    @Test
    void findByRuta_devuelve_dashboard() {
        assertThat(repo.findByRuta("/dashboard"))
                .isPresent()
                .get()
                .extracting(Submenu::getNombre)
                .isEqualTo("Dashboard");
    }
}
