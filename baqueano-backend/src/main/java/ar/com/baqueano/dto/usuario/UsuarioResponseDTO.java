package ar.com.baqueano.dto.usuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String username,
        String email,
        String nombre,
        String apellido,
        Long perfilId,
        String perfilNombre,
        Boolean activo,
        LocalDateTime ultimoLogin,
        Integer intentosFallidos,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}
