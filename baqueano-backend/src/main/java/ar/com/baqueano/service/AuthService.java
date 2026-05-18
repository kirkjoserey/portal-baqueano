package ar.com.baqueano.service;

import ar.com.baqueano.dto.auth.LoginRequestDTO;
import ar.com.baqueano.dto.auth.LoginResponseDTO;
import ar.com.baqueano.dto.auth.TokenResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO dto, String ipOrigen);

    TokenResponseDTO refresh(String refreshTokenRaw);

    void logout(String refreshTokenRaw);
}
