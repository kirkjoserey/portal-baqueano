package ar.com.baqueano.controller;

import ar.com.baqueano.dto.menu.MenuItemDTO;
import ar.com.baqueano.security.BaqueanoUserDetails;
import ar.com.baqueano.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService service;

    /**
     * Menu del perfil del usuario autenticado.
     * Disponible para cualquier usuario autenticado (cada perfil ve lo suyo).
     */
    @GetMapping("/mio")
    public ResponseEntity<List<MenuItemDTO>> mio(@AuthenticationPrincipal BaqueanoUserDetails user) {
        return ResponseEntity.ok(service.obtenerMenuPara(user.getPerfilId()));
    }
}
