package ar.com.baqueano.dto.contacto;

import ar.com.baqueano.domain.Contacto;

import java.time.LocalDateTime;

public record ContactoListItemDTO(
        Long id,
        String nombre,
        String email,
        String asunto,
        Contacto.Estado estado,
        LocalDateTime fechaCreacion
) {}
