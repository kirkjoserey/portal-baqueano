package ar.com.baqueano.dto.contacto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactoCreateDTO(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 30) String telefono,
        @Size(max = 150) String asunto,
        @NotBlank String mensaje
) {}
