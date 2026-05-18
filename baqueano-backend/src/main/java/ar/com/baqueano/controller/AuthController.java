package ar.com.baqueano.controller;

import ar.com.baqueano.dto.auth.LoginRequestDTO;
import ar.com.baqueano.dto.auth.LoginResponseDTO;
import ar.com.baqueano.dto.auth.RefreshRequestDTO;
import ar.com.baqueano.dto.auth.TokenResponseDTO;
import ar.com.baqueano.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto,
                                                  HttpServletRequest request) {
        return ResponseEntity.ok(authService.login(dto, request.getRemoteAddr()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO dto) {
        return ResponseEntity.ok(authService.refresh(dto.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDTO dto) {
        authService.logout(dto.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
