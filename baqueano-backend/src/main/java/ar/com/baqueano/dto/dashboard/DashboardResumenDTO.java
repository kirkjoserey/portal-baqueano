package ar.com.baqueano.dto.dashboard;

import ar.com.baqueano.dto.contacto.ContactoListItemDTO;

import java.util.List;

public record DashboardResumenDTO(
        long totalUsuarios,
        long totalPerfiles,
        long totalContactos,
        long contactosNuevos,
        List<ContactoListItemDTO> ultimosContactos
) {}
