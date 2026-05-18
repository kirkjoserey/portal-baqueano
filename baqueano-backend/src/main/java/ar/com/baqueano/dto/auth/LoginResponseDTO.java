package ar.com.baqueano.dto.auth;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        Long usuarioId,
        String username,
        Long perfilId,
        String perfilNombre
) {}
