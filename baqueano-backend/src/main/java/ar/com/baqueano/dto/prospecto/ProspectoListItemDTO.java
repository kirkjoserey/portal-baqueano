package ar.com.baqueano.dto.prospecto;

import ar.com.baqueano.domain.Prospecto;

public record ProspectoListItemDTO(
        Long id,
        String nombre,
        String apellido,
        String empresa,
        String email,
        String telefono,
        Prospecto.Estado estado,
        Prospecto.Origen origen,
        Boolean activo
) {}
