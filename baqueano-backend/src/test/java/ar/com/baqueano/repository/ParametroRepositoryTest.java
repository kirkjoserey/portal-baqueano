package ar.com.baqueano.repository;

import ar.com.baqueano.config.JpaConfig;
import ar.com.baqueano.domain.Parametro;
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
class ParametroRepositoryTest {

    @Autowired
    private ParametroRepository repo;

    @Test
    void findByClave_recupera_seed_app_nombre() {
        Optional<Parametro> p = repo.findByClave("app.nombre");

        assertThat(p).isPresent();
        assertThat(p.get().getValor()).isEqualTo("Baqueano");
        assertThat(p.get().getTipoDato()).isEqualTo(Parametro.TipoDato.STRING);
        assertThat(p.get().getEditable()).isTrue();
    }

    @Test
    void findByClave_login_intentos_max_devuelve_tipo_NUMBER() {
        Optional<Parametro> p = repo.findByClave("login.intentos.max");

        assertThat(p).isPresent();
        assertThat(p.get().getValor()).isEqualTo("5");
        assertThat(p.get().getTipoDato()).isEqualTo(Parametro.TipoDato.NUMBER);
    }

    @Test
    void existsByClave_detecta_inexistente() {
        assertThat(repo.existsByClave("app.nombre")).isTrue();
        assertThat(repo.existsByClave("clave.que.no.existe")).isFalse();
    }
}
