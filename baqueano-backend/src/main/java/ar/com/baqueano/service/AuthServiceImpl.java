package ar.com.baqueano.service;

import ar.com.baqueano.domain.RefreshToken;
import ar.com.baqueano.domain.Usuario;
import ar.com.baqueano.dto.auth.LoginRequestDTO;
import ar.com.baqueano.dto.auth.LoginResponseDTO;
import ar.com.baqueano.dto.auth.TokenResponseDTO;
import ar.com.baqueano.repository.ParametroRepository;
import ar.com.baqueano.repository.RefreshTokenRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import ar.com.baqueano.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final int DEFAULT_MAX_INTENTOS = 5;

    private final UsuarioRepository usuarioRepo;
    private final RefreshTokenRepository refreshRepo;
    private final ParametroRepository parametroRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto, String ipOrigen) {
        Usuario u = usuarioRepo.findByUsername(dto.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!Boolean.TRUE.equals(u.getActivo())) {
            throw new DisabledException("Usuario inactivo");
        }

        int maxIntentos = obtenerMaxIntentos();
        if (u.getIntentosFallidos() != null && u.getIntentosFallidos() >= maxIntentos) {
            throw new LockedException("Usuario bloqueado por intentos fallidos");
        }

        if (!passwordEncoder.matches(dto.password(), u.getPasswordHash())) {
            u.setIntentosFallidos((u.getIntentosFallidos() == null ? 0 : u.getIntentosFallidos()) + 1);
            log.info("Login fallido para usuario={} intentos={}", u.getUsername(), u.getIntentosFallidos());
            throw new BadCredentialsException("Credenciales invalidas");
        }

        // Login exitoso
        u.setIntentosFallidos(0);
        u.setUltimoLogin(LocalDateTime.now());

        String access = tokenProvider.generarAccessToken(u);
        String refreshRaw = tokenProvider.generarRefreshTokenRaw();

        RefreshToken rt = new RefreshToken();
        rt.setUsuario(u);
        rt.setToken(sha256(refreshRaw));
        rt.setExpiraEn(LocalDateTime.ofInstant(
                Instant.now().plusMillis(tokenProvider.getRefreshExpirationMs()),
                ZoneId.systemDefault()));
        rt.setIpOrigen(ipOrigen);
        refreshRepo.save(rt);

        return new LoginResponseDTO(
                access,
                refreshRaw,
                "Bearer",
                tokenProvider.getExpirationMs() / 1000,
                u.getId(),
                u.getUsername(),
                u.getPerfil().getId(),
                u.getPerfil().getNombre());
    }

    @Override
    @Transactional
    public TokenResponseDTO refresh(String refreshTokenRaw) {
        String hash = sha256(refreshTokenRaw);
        RefreshToken rt = refreshRepo.findByToken(hash)
                .orElseThrow(() -> new BadCredentialsException("Refresh token invalido"));

        if (Boolean.TRUE.equals(rt.getRevocado())) {
            throw new BadCredentialsException("Refresh token revocado");
        }
        if (rt.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token expirado");
        }
        if (!Boolean.TRUE.equals(rt.getUsuario().getActivo())) {
            throw new DisabledException("Usuario inactivo");
        }

        String access = tokenProvider.generarAccessToken(rt.getUsuario());
        return new TokenResponseDTO(access, "Bearer", tokenProvider.getExpirationMs() / 1000);
    }

    @Override
    @Transactional
    public void logout(String refreshTokenRaw) {
        String hash = sha256(refreshTokenRaw);
        refreshRepo.findByToken(hash).ifPresent(rt -> {
            rt.setRevocado(Boolean.TRUE);
            rt.setFechaRevocacion(LocalDateTime.now());
        });
    }

    private int obtenerMaxIntentos() {
        return parametroRepo.findByClave("login.intentos.max")
                .map(p -> {
                    try {
                        return Integer.parseInt(p.getValor());
                    } catch (NumberFormatException e) {
                        return DEFAULT_MAX_INTENTOS;
                    }
                })
                .orElse(DEFAULT_MAX_INTENTOS);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
