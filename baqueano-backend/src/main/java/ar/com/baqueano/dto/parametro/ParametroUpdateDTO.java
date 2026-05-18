package ar.com.baqueano.dto.parametro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Clave y tipoDato no son editables (definen la semantica del parametro).
 */
public record ParametroUpdateDTO(
        @NotBlank @Size(max = 500) String valor,
        @Size(max = 255) String descripcion,
        Boolean editable
) {}
