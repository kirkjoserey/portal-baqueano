package ar.com.baqueano.dto.menu;

import java.util.List;

public record MenuItemDTO(
        String nombre,
        String icono,
        Integer orden,
        List<SubmenuItemDTO> submenus
) {}
