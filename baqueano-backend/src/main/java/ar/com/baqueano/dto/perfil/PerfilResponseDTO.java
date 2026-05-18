package ar.com.baqueano.dto.perfil;

import java.time.LocalDateTime;

public record PerfilResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}
