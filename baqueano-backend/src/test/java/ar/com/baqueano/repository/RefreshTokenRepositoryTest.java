package ar.com.baqueano.repository;

import ar.com.baqueano.config.JpaConfig;
import ar.com.baqueano.domain.RefreshToken;
import ar.com.baqueano.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Test
    void save_y_findByToken_funciona_con_admin() {
        Usuario admin = usuarioRepo.findByUsername("admin").orElseThrow();

        RefreshToken rt = new RefreshToken();
        rt.setUsuario(admin);
        rt.setToken("sha256-test-" + System.nanoTime());
        rt.setExpiraEn(LocalDateTime.now().plusDays(1));
        rt.setIpOrigen("127.0.0.1");

        RefreshToken guardado = repo.save(rt);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getRevocado()).isFalse();
        assertThat(guardado.getFechaCreacion()).isNotNull();
        assertThat(guardado.getUsuarioCreacion()).isEqualTo("system");

        Optional<RefreshToken> recuperado = repo.findByToken(rt.getToken());
        assertThat(recuperado).isPresent();
        assertThat(recuperado.get().getUsuario().getUsername()).isEqualTo("admin");
    }

    @Test
    void findByToken_inexistente_devuelve_empty() {
        assertThat(repo.findByToken("no-existe-este-token")).isEmpty();
    }
}
