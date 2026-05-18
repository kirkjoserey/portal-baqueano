package ar.com.baqueano.repository;

import ar.com.baqueano.config.JpaConfig;
import ar.com.baqueano.domain.Perfil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
class PerfilRepositoryTest {

    @Autowired
    private PerfilRepository repo;

    @Test
    void findByNombre_devuelve_el_perfil_admin_del_seed() {
        Optional<Perfil> p = repo.findByNombre("ADMIN");

        assertThat(p).isPresent();
        assertThat(p.get().getDescripcion()).isEqualTo("Administrador con acceso total");
        assertThat(p.get().getActivo()).isTrue();
    }

    @Test
    void save_completa_auditoria_con_system() {
        Perfil nuevo = new Perfil();
        nuevo.setNombre("PRUEBA_AUDIT");
        nuevo.setDescripcion("Verifica auditing");

        Perfil guardado = repo.save(nuevo);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getFechaModificacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system");
        assertThat(guardado.getUsuarioModificacion()).isEqualTo("system");
    }

    @Test
    void existsByNombre_distingue_existente_de_inexistente() {
        assertThat(repo.existsByNombre("ADMIN")).isTrue();
        assertThat(repo.existsByNombre("INEXISTENTE_XYZ")).isFalse();
    }
}
