package ar.com.baqueano.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Respuesta uniforme para errores. Coincide con la estructura indicada en la spec 5.7.
 * fieldErrors viaja solo en errores de validacion (400).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldErrorDTO> fieldErrors
) {

    public record FieldErrorDTO(String field, String message) {}
}
