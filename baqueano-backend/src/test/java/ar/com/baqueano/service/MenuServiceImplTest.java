package ar.com.baqueano.service;

import ar.com.baqueano.domain.Menu;
import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.Submenu;
import ar.com.baqueano.domain.SubmenuPerfil;
import ar.com.baqueano.domain.SubmenuPerfilId;
import ar.com.baqueano.dto.menu.MenuItemDTO;
import ar.com.baqueano.dto.menu.SubmenuItemDTO;
import ar.com.baqueano.repository.SubmenuPerfilRepository;
import ar.com.baqueano.repository.SubmenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private SubmenuRepository submenuRepo;
    @Mock
    private SubmenuPerfilRepository spRepo;

    @InjectMocks
    private MenuServiceImpl service;

    private Menu menuPrincipal;
    private Submenu sDashboard;
    private Submenu sUsuarios;
    private Perfil admin;

    @BeforeEach
    void setUp() {
        menuPrincipal = new Menu();
        menuPrincipal.setId(1L);
        menuPrincipal.setNombre("MENU PRINCIPAL");
        menuPrincipal.setOrden(1);

        sDashboard = new Submenu();
        sDashboard.setId(10L);
        sDashboard.setMenu(menuPrincipal);
        sDashboard.setNombre("Dashboard");
        sDashboard.setRuta("/dashboard");
        sDashboard.setIcono("home");
        sDashboard.setOrden(1);

        sUsuarios = new Submenu();
        sUsuarios.setId(20L);
        sUsuarios.setMenu(menuPrincipal);
        sUsuarios.setNombre("Usuarios");
        sUsuarios.setRuta("/usuarios");
        sUsuarios.setIcono("users");
        sUsuarios.setOrden(3);

        admin = new Perfil();
        admin.setId(1L);
        admin.setNombre("ADMIN");
    }

    @Test
    void obtenerMenuPara_admin_agrupa_submenus_bajo_menu_padre_con_permisos() {
        when(submenuRepo.findMenuPorPerfil(1L))
                .thenReturn(List.of(sDashboard, sUsuarios));
        when(spRepo.findByPerfilId(1L))
                .thenReturn(List.of(permiso(sDashboard, admin, true, false, false, false),
                                    permiso(sUsuarios,  admin, true, true,  true,  true)));

        List<MenuItemDTO> menus = service.obtenerMenuPara(1L);

        assertThat(menus).hasSize(1);
        MenuItemDTO m = menus.get(0);
        assertThat(m.nombre()).isEqualTo("MENU PRINCIPAL");
        assertThat(m.submenus()).hasSize(2);

        SubmenuItemDTO dash = m.submenus().get(0);
        assertThat(dash.ruta()).isEqualTo("/dashboard");
        assertThat(dash.puedeVer()).isTrue();
        assertThat(dash.puedeCrear()).isFalse();

        SubmenuItemDTO users = m.submenus().get(1);
        assertThat(users.ruta()).isEqualTo("/usuarios");
        assertThat(users.puedeCrear()).isTrue();
        assertThat(users.puedeEliminar()).isTrue();
    }

    @Test
    void obtenerMenuPara_sin_submenus_devuelve_lista_vacia() {
        when(submenuRepo.findMenuPorPerfil(99L)).thenReturn(List.of());
        when(spRepo.findByPerfilId(99L)).thenReturn(List.of());

        assertThat(service.obtenerMenuPara(99L)).isEmpty();
    }

    private SubmenuPerfil permiso(Submenu s, Perfil p, boolean ver, boolean crear,
                                  boolean editar, boolean eliminar) {
        SubmenuPerfil sp = new SubmenuPerfil();
        sp.setId(new SubmenuPerfilId(s.getId(), p.getId()));
        sp.setSubmenu(s);
        sp.setPerfil(p);
        sp.setPuedeVer(ver);
        sp.setPuedeCrear(crear);
        sp.setPuedeEditar(editar);
        sp.setPuedeEliminar(eliminar);
        return sp;
    }
}
