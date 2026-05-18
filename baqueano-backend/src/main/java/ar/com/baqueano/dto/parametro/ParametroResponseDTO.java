package ar.com.baqueano.dto.parametro;

import ar.com.baqueano.domain.Parametro;

import java.time.LocalDateTime;

public record ParametroResponseDTO(
        Long id,
        String clave,
        String valor,
        String descripcion,
        Parametro.TipoDato tipoDato,
        Boolean editable,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaModificacion
) {}
