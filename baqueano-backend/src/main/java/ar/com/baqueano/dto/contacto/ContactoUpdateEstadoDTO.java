package ar.com.baqueano.dto.contacto;

import ar.com.baqueano.domain.Contacto;
import jakarta.validation.constraints.NotNull;

public record ContactoUpdateEstadoDTO(
        @NotNull Contacto.Estado estado
) {}
