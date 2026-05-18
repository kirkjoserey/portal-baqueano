package ar.com.baqueano.dto.menu;

/**
 * Item de menu para un perfil dado, con sus permisos. Lo consume
 * el frontend en /menu/mio (Fase 6) para construir el sidebar y
 * habilitar/deshabilitar acciones por boton.
 */
public record SubmenuItemDTO(
        String nombre,
        String ruta,
        String icono,
        Integer orden,
        Boolean puedeVer,
        Boolean puedeCrear,
        Boolean puedeEditar,
        Boolean puedeEliminar
) {}
