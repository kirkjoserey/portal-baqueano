package ar.com.baqueano.dto.parametro;

import ar.com.baqueano.domain.Parametro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParametroCreateDTO(
        @NotBlank @Size(max = 100) String clave,
        @NotBlank @Size(max = 500) String valor,
        @Size(max = 255) String descripcion,
        @NotNull Parametro.TipoDato tipoDato,
        Boolean editable
) {}
