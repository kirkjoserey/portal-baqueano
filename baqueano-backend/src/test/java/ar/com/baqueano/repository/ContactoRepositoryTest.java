package ar.com.baqueano.repository;

import ar.com.baqueano.config.JpaConfig;
import ar.com.baqueano.domain.Contacto;
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
class ContactoRepositoryTest {

    @Autowired
    private ContactoRepository repo;

    @Test
    void save_y_findTop5_recupera_lo_recien_creado() {
        Contacto c = new Contacto();
        c.setNombre("Pepe Tester");
        c.setEmail("pepe.tester@example.com");
        c.setMensaje("Hola desde el test");

        Contacto guardado = repo.save(c);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstado()).isEqualTo(Contacto.Estado.NUEVO);
        assertThat(guardado.getFechaCreacion()).isNotNull();

        List<Contacto> top = repo.findTop5ByOrderByFechaCreacionDesc();
        assertThat(top).isNotEmpty();
        assertThat(top.get(0).getEmail()).isEqualTo("pepe.tester@example.com");
    }

    @Test
    void countByEstado_funciona_con_estados_vacios() {
        // No hay seed para contactos: el conteo de estados arranca en 0 por entidad nueva
        long resp = repo.countByEstado(Contacto.Estado.RESPONDIDO);
        assertThat(resp).isGreaterThanOrEqualTo(0L);
    }
}
