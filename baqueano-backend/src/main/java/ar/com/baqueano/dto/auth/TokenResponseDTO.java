package ar.com.baqueano.dto.auth;

public record TokenResponseDTO(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {}
