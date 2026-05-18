package ar.com.baqueano.dto.contacto;

import ar.com.baqueano.domain.Contacto;

import java.time.LocalDateTime;

public record ContactoResponseDTO(
        Long id,
        String nombre,
        String email,
        String telefono,
        String asunto,
        String mensaje,
        Contacto.Estado estado,
        String ipOrigen,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}
