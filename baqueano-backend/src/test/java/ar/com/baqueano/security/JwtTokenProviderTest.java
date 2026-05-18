package ar.com.baqueano.security;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.Usuario;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "una-clave-de-prueba-con-mas-de-256-bits-de-largo-suficiente-para-HS256";

    private JwtTokenProvider provider;
    private Usuario admin;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, 1_800_000L, 86_400_000L);

        Perfil p = new Perfil();
        p.setId(1L);
        p.setNombre("ADMIN");

        admin = new Usuario();
        admin.setId(42L);
        admin.setUsername("admin");
        admin.setPerfil(p);
    }

    @Test
    void generarAccessToken_lleva_claims_estandar() {
        String token = provider.generarAccessToken(admin);
        assertThat(token).isNotBlank();

        Claims claims = provider.parseClaims(token);
        assertThat(claims.getSubject()).isEqualTo("admin");
        assertThat(claims.get("uid", Long.class)).isEqualTo(42L);
        assertThat(claims.get("perfilId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("perfil", String.class)).isEqualTo("ADMIN");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void validar_token_valido_devuelve_true() {
        String token = provider.generarAccessToken(admin);
        assertThat(provider.validar(token)).isTrue();
    }

    @Test
    void validar_token_invalido_devuelve_false() {
        assertThat(provider.validar("not-a-jwt")).isFalse();
        assertThat(provider.validar("a.b.c")).isFalse();
        assertThat(provider.validar("")).isFalse();
    }

    @Test
    void validar_token_emitido_por_otro_secreto_devuelve_false() {
        JwtTokenProvider otro = new JwtTokenProvider(
                "otra-clave-distinta-de-256-bits-igualmente-valida-para-HS256-correcto", 1000L, 1000L);
        String tokenAjeno = otro.generarAccessToken(admin);
        assertThat(provider.validar(tokenAjeno)).isFalse();
    }

    @Test
    void validar_token_expirado_devuelve_false() throws InterruptedException {
        JwtTokenProvider efimero = new JwtTokenProvider(SECRET, 1L, 1L);
        String token = efimero.generarAccessToken(admin);
        Thread.sleep(50);
        assertThat(efimero.validar(token)).isFalse();
    }

    @Test
    void generarRefreshTokenRaw_genera_strings_unicos_y_no_vacios() {
        String t1 = provider.generarRefreshTokenRaw();
        String t2 = provider.generarRefreshTokenRaw();
        assertThat(t1).isNotBlank();
        assertThat(t2).isNotBlank();
        assertThat(t1).isNotEqualTo(t2);
    }
}
