package ar.com.baqueano.security;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.Submenu;
import ar.com.baqueano.domain.SubmenuPerfil;
import ar.com.baqueano.domain.SubmenuPerfilId;
import ar.com.baqueano.domain.Usuario;
import ar.com.baqueano.repository.SubmenuPerfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermisoEvaluatorTest {

    @Mock
    private SubmenuPerfilRepository spRepo;

    @InjectMocks
    private PermisoEvaluator evaluator;

    private Authentication adminAuth;
    private SubmenuPerfil permisoTotal;

    @BeforeEach
    void setUp() {
        Perfil admin = new Perfil();
        admin.setId(1L);
        admin.setNombre("ADMIN");

        Usuario u = new Usuario();
        u.setId(10L);
        u.setUsername("admin");
        u.setPerfil(admin);
        u.setActivo(true);

        BaqueanoUserDetails ud = new BaqueanoUserDetails(u);
        adminAuth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());

        Submenu s = new Submenu();
        s.setId(100L);
        s.setRuta("/usuarios");

        permisoTotal = new SubmenuPerfil();
        permisoTotal.setId(new SubmenuPerfilId(100L, 1L));
        permisoTotal.setSubmenu(s);
        permisoTotal.setPerfil(admin);
        permisoTotal.setPuedeVer(true);
        permisoTotal.setPuedeCrear(true);
        permisoTotal.setPuedeEditar(true);
        permisoTotal.setPuedeEliminar(true);
    }

    @Test
    void admin_puede_crear_y_eliminar_en_usuarios() {
        when(spRepo.findPermisos(1L, "/usuarios")).thenReturn(Optional.of(permisoTotal));

        assertThat(evaluator.puede(adminAuth, "/usuarios", "VER")).isTrue();
        assertThat(evaluator.puede(adminAuth, "/usuarios", "CREAR")).isTrue();
        assertThat(evaluator.puede(adminAuth, "/usuarios", "EDITAR")).isTrue();
        assertThat(evaluator.puede(adminAuth, "/usuarios", "ELIMINAR")).isTrue();
    }

    @Test
    void permiso_inexistente_para_ruta_devuelve_false() {
        when(spRepo.findPermisos(1L, "/no-existe")).thenReturn(Optional.empty());
        assertThat(evaluator.puede(adminAuth, "/no-existe", "VER")).isFalse();
    }

    @Test
    void permiso_solo_lectura_rechaza_crear_y_eliminar() {
        SubmenuPerfil soloLectura = new SubmenuPerfil();
        soloLectura.setPuedeVer(true);
        soloLectura.setPuedeCrear(false);
        soloLectura.setPuedeEditar(false);
        soloLectura.setPuedeEliminar(false);
        when(spRepo.findPermisos(1L, "/dashboard")).thenReturn(Optional.of(soloLectura));

        assertThat(evaluator.puede(adminAuth, "/dashboard", "VER")).isTrue();
        assertThat(evaluator.puede(adminAuth, "/dashboard", "CREAR")).isFalse();
        assertThat(evaluator.puede(adminAuth, "/dashboard", "EDITAR")).isFalse();
        assertThat(evaluator.puede(adminAuth, "/dashboard", "ELIMINAR")).isFalse();
    }

    @Test
    void authentication_null_devuelve_false() {
        assertThat(evaluator.puede(null, "/dashboard", "VER")).isFalse();
    }

    @Test
    void accion_desconocida_devuelve_false() {
        when(spRepo.findPermisos(1L, "/usuarios")).thenReturn(Optional.of(permisoTotal));
        assertThat(evaluator.puede(adminAuth, "/usuarios", "ALGO_INVENTADO")).isFalse();
    }
}
