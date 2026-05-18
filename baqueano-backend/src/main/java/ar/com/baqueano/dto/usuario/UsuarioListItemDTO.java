package ar.com.baqueano.dto.usuario;

public record UsuarioListItemDTO(
        Long id,
        String username,
        String email,
        String nombre,
        String apellido,
        String perfilNombre,
        Boolean activo
) {}
