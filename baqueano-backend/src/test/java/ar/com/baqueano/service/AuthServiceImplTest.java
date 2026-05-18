package ar.com.baqueano.service;

import ar.com.baqueano.domain.Parametro;
import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.RefreshToken;
import ar.com.baqueano.domain.Usuario;
import ar.com.baqueano.dto.auth.LoginRequestDTO;
import ar.com.baqueano.dto.auth.LoginResponseDTO;
import ar.com.baqueano.repository.ParametroRepository;
import ar.com.baqueano.repository.RefreshTokenRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import ar.com.baqueano.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepo;
    @Mock
    private RefreshTokenRepository refreshRepo;
    @Mock
    private ParametroRepository parametroRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;

    private AuthServiceImpl service;

    private Usuario admin;
    private Perfil perfilAdmin;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(usuarioRepo, refreshRepo, parametroRepo, passwordEncoder, tokenProvider);

        perfilAdmin = new Perfil();
        perfilAdmin.setId(1L);
        perfilAdmin.setNombre("ADMIN");

        admin = new Usuario();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash("$2a$10$ALGO");
        admin.setPerfil(perfilAdmin);
        admin.setActivo(true);
        admin.setIntentosFallidos(0);
    }

    @Test
    void login_credenciales_validas_emite_tokens_y_resetea_intentos() {
        admin.setIntentosFallidos(3);

        when(usuarioRepo.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(parametroRepo.findByClave("login.intentos.max"))
                .thenReturn(Optional.of(paramIntentos(5)));
        when(passwordEncoder.matches("admin123", "$2a$10$ALGO")).thenReturn(true);
        when(tokenProvider.generarAccessToken(admin)).thenReturn("access.jwt.token");
        when(tokenProvider.generarRefreshTokenRaw()).thenReturn("refresh-raw");
        when(tokenProvider.getRefreshExpirationMs()).thenReturn(86_400_000L);
        when(tokenProvider.getExpirationMs()).thenReturn(1_800_000L);

        LoginResponseDTO res = service.login(new LoginRequestDTO("admin", "admin123"), "127.0.0.1");

        assertThat(res.accessToken()).isEqualTo("access.jwt.token");
        assertThat(res.refreshToken()).isEqualTo("refresh-raw");
        assertThat(res.perfilNombre()).isEqualTo("ADMIN");
        assertThat(res.expiresInSeconds()).isEqualTo(1800L);

        assertThat(admin.getIntentosFallidos()).isZero();
        assertThat(admin.getUltimoLogin()).isNotNull();
        verify(refreshRepo).save(any(RefreshToken.class));
    }

    @Test
    void login_password_incorrecto_incrementa_intentos_y_lanza_BadCredentials() {
        when(usuarioRepo.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(parametroRepo.findByClave("login.intentos.max"))
                .thenReturn(Optional.of(paramIntentos(5)));
        when(passwordEncoder.matches("wrong", "$2a$10$ALGO")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequestDTO("admin", "wrong"), "127.0.0.1"))
                .isInstanceOf(BadCredentialsException.class);

        assertThat(admin.getIntentosFallidos()).isEqualTo(1);
        verify(refreshRepo, never()).save(any());
    }

    @Test
    void login_usuario_bloqueado_por_intentos_lanza_LockedException() {
        admin.setIntentosFallidos(5);
        when(usuarioRepo.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(parametroRepo.findByClave("login.intentos.max"))
                .thenReturn(Optional.of(paramIntentos(5)));

        assertThatThrownBy(() -> service.login(new LoginRequestDTO("admin", "admin123"), "127.0.0.1"))
                .isInstanceOf(LockedException.class);
    }

    @Test
    void login_usuario_inactivo_lanza_DisabledException() {
        admin.setActivo(false);
        when(usuarioRepo.findByUsername("admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.login(new LoginRequestDTO("admin", "x"), "127.0.0.1"))
                .isInstanceOf(DisabledException.class);
    }

    @Test
    void login_usuario_inexistente_lanza_BadCredentials() {
        when(usuarioRepo.findByUsername("nadie")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequestDTO("nadie", "x"), "127.0.0.1"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void logout_revoca_el_refresh_token_si_existe() {
        RefreshToken rt = new RefreshToken();
        rt.setUsuario(admin);
        rt.setRevocado(false);
        // El service hashea "raw-token" antes de buscar; el mock acepta cualquier hash
        when(refreshRepo.findByToken(any(String.class))).thenReturn(Optional.of(rt));

        service.logout("raw-token");

        assertThat(rt.getRevocado()).isTrue();
        assertThat(rt.getFechaRevocacion()).isNotNull();
    }

    private Parametro paramIntentos(int valor) {
        Parametro p = new Parametro();
        p.setClave("login.intentos.max");
        p.setValor(String.valueOf(valor));
        p.setTipoDato(Parametro.TipoDato.NUMBER);
        return p;
    }
}
