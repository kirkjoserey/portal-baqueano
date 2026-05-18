package ar.com.baqueano.controller;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.Usuario;
import ar.com.baqueano.dto.menu.MenuItemDTO;
import ar.com.baqueano.dto.menu.SubmenuItemDTO;
import ar.com.baqueano.security.BaqueanoUserDetails;
import ar.com.baqueano.service.MenuService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MenuController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "ar\\.com\\.baqueano\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MenuService service;

    @BeforeEach
    void configurarPrincipalAdmin() {
        Perfil p = new Perfil();
        p.setId(1L);
        p.setNombre("ADMIN");
        Usuario u = new Usuario();
        u.setId(1L);
        u.setUsername("admin");
        u.setActivo(true);
        u.setPerfil(p);

        BaqueanoUserDetails ud = new BaqueanoUserDetails(u);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities()));
    }

    @AfterEach
    void limpiarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void mio_devuelve_menu_del_perfil_del_principal() throws Exception {
        MenuItemDTO menu = new MenuItemDTO("MENU PRINCIPAL", null, 1, List.of(
                new SubmenuItemDTO("Dashboard", "/dashboard", "home", 1, true, true, true, true)));
        when(service.obtenerMenuPara(1L)).thenReturn(List.of(menu));

        mockMvc.perform(get("/api/v1/menu/mio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("MENU PRINCIPAL"))
                .andExpect(jsonPath("$[0].submenus[0].ruta").value("/dashboard"))
                .andExpect(jsonPath("$[0].submenus[0].puedeCrear").value(true));
    }
}
