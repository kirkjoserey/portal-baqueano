package ar.com.baqueano.dto.prospecto;

import ar.com.baqueano.domain.Prospecto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProspectoUpdateDTO(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 100) String apellido,
        @Size(max = 150)           String empresa,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 30)            String telefono,
        @NotNull                   Prospecto.Estado estado,
        @NotNull                   Prospecto.Origen origen,
        String notas,
        Boolean activo
) {}
