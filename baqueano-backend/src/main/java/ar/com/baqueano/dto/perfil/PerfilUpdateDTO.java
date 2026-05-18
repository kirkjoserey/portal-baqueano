package ar.com.baqueano.dto.perfil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilUpdateDTO(
        @NotBlank @Size(max = 50) String nombre,
        @Size(max = 255) String descripcion,
        Boolean activo
) {}
