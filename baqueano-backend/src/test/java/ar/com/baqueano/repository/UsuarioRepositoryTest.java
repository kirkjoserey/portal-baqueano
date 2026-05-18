package ar.com.baqueano.repository;

import ar.com.baqueano.config.JpaConfig;
import ar.com.baqueano.domain.Usuario;
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
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repo;

    @Test
    void findByUsername_admin_carga_perfil_lazy() {
        Optional<Usuario> u = repo.findByUsername("admin");

        assertThat(u).isPresent();
        assertThat(u.get().getEmail()).isEqualTo("admin@baqueano.local");
        // El acceso al perfil dispara el LAZY load
        assertThat(u.get().getPerfil().getNombre()).isEqualTo("ADMIN");
        assertThat(u.get().getActivo()).isTrue();
        assertThat(u.get().getIntentosFallidos()).isZero();
    }

    @Test
    void existsByUsername_y_existsByEmail() {
        assertThat(repo.existsByUsername("admin")).isTrue();
        assertThat(repo.existsByUsername("nadie")).isFalse();
        assertThat(repo.existsByEmail("admin@baqueano.local")).isTrue();
        assertThat(repo.existsByEmail("nadie@noexiste.com")).isFalse();
    }

    @Test
    void countByPerfilId_admin_tiene_un_usuario() {
        // El seed crea exactamente un usuario (admin) con perfil_id=1
        assertThat(repo.countByPerfilId(1L)).isEqualTo(1L);
        assertThat(repo.countByPerfilId(2L)).isZero();
        assertThat(repo.countByPerfilId(3L)).isZero();
    }
}
