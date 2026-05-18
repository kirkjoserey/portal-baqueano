package ar.com.baqueano.security;

import ar.com.baqueano.dto.error.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Provee el AuthenticationEntryPoint (para requests no autenticados) y
 * el AccessDeniedHandler (para requests autenticados pero sin permiso)
 * con la misma estructura ErrorResponseDTO que usa el GlobalExceptionHandler.
 */
@Component
@RequiredArgsConstructor
public class JsonAuthHandlers {

    private final ObjectMapper objectMapper;

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            ErrorResponseDTO body = new ErrorResponseDTO(
                    Instant.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "UNAUTHENTICATED",
                    "Se requiere autenticacion para acceder al recurso",
                    request.getRequestURI(),
                    null);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), body);
        };
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            ErrorResponseDTO body = new ErrorResponseDTO(
                    Instant.now(),
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase(),
                    "FORBIDDEN",
                    "Acceso denegado",
                    request.getRequestURI(),
                    null);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), body);
        };
    }
}
