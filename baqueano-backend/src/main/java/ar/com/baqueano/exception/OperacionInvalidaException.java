package ar.com.baqueano.exception;

/**
 * Violacion de regla de negocio: estado invalido, duplicado, FK que rompe invariante, etc.
 * Mapeada a HTTP 409/422 en el @RestControllerAdvice de la Fase 6.
 */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
