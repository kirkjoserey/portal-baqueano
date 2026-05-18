package ar.com.baqueano.service;

import ar.com.baqueano.dto.menu.MenuItemDTO;

import java.util.List;

public interface MenuService {

    /**
     * Construye el menu jerarquico (Menu -> Submenus) para el perfil dado,
     * con los permisos puede_ver/crear/editar/eliminar de cada submenu.
     * Lo consume el frontend en /menu/mio (Fase 6).
     */
    List<MenuItemDTO> obtenerMenuPara(Long perfilId);
}
