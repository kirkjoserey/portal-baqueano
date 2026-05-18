package ar.com.baqueano.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Sin password ni username: el cambio de contrasenia tiene su propio endpoint
 * (Fase 5), y el username es inmutable una vez creado.
 */
public record UsuarioUpdateDTO(
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @NotNull Long perfilId,
        Boolean activo
) {}
