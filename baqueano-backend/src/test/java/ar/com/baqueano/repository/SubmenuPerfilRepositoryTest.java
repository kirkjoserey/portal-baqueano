package ar.com.baqueano.repository;

import ar.com.baqueano.config.JpaConfig;
import ar.com.baqueano.domain.SubmenuPerfil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
class SubmenuPerfilRepositoryTest {

    @Autowired
    private SubmenuPerfilRepository repo;

    @Test
    void findByPerfilId_admin_devuelve_los_6_permisos() {
        List<SubmenuPerfil> permisos = repo.findByPerfilId(1L);
        assertThat(permisos).hasSize(6);
        assertThat(permisos).allSatisfy(sp -> {
            assertThat(sp.getPuedeVer()).isTrue();
            assertThat(sp.getPuedeCrear()).isTrue();
            assertThat(sp.getPuedeEditar()).isTrue();
            assertThat(sp.getPuedeEliminar()).isTrue();
        });
    }

    @Test
    void findPermisos_admin_sobre_usuarios_tiene_todos_los_permisos() {
        Optional<SubmenuPerfil> sp = repo.findPermisos(1L, "/usuarios");

        assertThat(sp).isPresent();
        assertThat(sp.get().getPuedeVer()).isTrue();
        assertThat(sp.get().getPuedeCrear()).isTrue();
        assertThat(sp.get().getPuedeEditar()).isTrue();
        assertThat(sp.get().getPuedeEliminar()).isTrue();
    }

    @Test
    void findPermisos_consulta_sobre_dashboard_es_solo_lectura() {
        Optional<SubmenuPerfil> sp = repo.findPermisos(3L, "/dashboard");

        assertThat(sp).isPresent();
        assertThat(sp.get().getPuedeVer()).isTrue();
        assertThat(sp.get().getPuedeCrear()).isFalse();
        assertThat(sp.get().getPuedeEditar()).isFalse();
        assertThat(sp.get().getPuedeEliminar()).isFalse();
    }

    @Test
    void findPermisos_consulta_sobre_parametros_no_existe() {
        // Seed excluye /parametros para perfil CONSULTA
        Optional<SubmenuPerfil> sp = repo.findPermisos(3L, "/parametros");
        assertThat(sp).isEmpty();
    }
}
