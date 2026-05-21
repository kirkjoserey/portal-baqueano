package ar.com.baqueano.dto.prospecto;

import ar.com.baqueano.domain.Prospecto;

import java.time.LocalDateTime;

public record ProspectoResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String empresa,
        String email,
        String telefono,
        Prospecto.Estado estado,
        Prospecto.Origen origen,
        String notas,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}
