package ar.com.baqueano.service;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.Usuario;
import ar.com.baqueano.dto.usuario.UsuarioCreateDTO;
import ar.com.baqueano.dto.usuario.UsuarioResponseDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.mapper.UsuarioMapper;
import ar.com.baqueano.repository.PerfilRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepo;
    @Mock
    private PerfilRepository perfilRepo;
    @Mock
    private PasswordEncoder passwordEncoder;

    private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);
    private UsuarioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UsuarioServiceImpl(usuarioRepo, perfilRepo, mapper, passwordEncoder);
    }

    @Test
    void crear_hashea_password_y_completa_defaults() {
        Perfil admin = new Perfil();
        admin.setId(1L);
        admin.setNombre("ADMIN");

        when(usuarioRepo.existsByUsername("pepe")).thenReturn(false);
        when(usuarioRepo.existsByEmail("pepe@x.com")).thenReturn(false);
        when(perfilRepo.findById(1L)).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode("contrasenia12345")).thenReturn("$2a$10$HASHED");
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(50L);
            return u;
        });

        UsuarioResponseDTO out = service.crear(new UsuarioCreateDTO(
                "pepe", "pepe@x.com", "contrasenia12345", "Pepe", "Test", 1L, null));

        assertThat(out.id()).isEqualTo(50L);
        assertThat(out.perfilNombre()).isEqualTo("ADMIN");
        assertThat(out.activo()).isTrue();

        ArgumentCaptor<Usuario> cap = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepo).save(cap.capture());
        assertThat(cap.getValue().getPasswordHash()).isEqualTo("$2a$10$HASHED");
        assertThat(cap.getValue().getIntentosFallidos()).isZero();
    }

    @Test
    void crear_falla_si_username_existe() {
        when(usuarioRepo.existsByUsername("admin")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(new UsuarioCreateDTO(
                "admin", "x@x.com", "12345678", "x", "x", 1L, null)))
                .isInstanceOf(OperacionInvalidaException.class)
                .hasMessageContaining("admin");

        verify(usuarioRepo, never()).save(any());
    }

    @Test
    void crear_falla_si_email_existe() {
        when(usuarioRepo.existsByUsername("pepe")).thenReturn(false);
        when(usuarioRepo.existsByEmail("dup@x.com")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(new UsuarioCreateDTO(
                "pepe", "dup@x.com", "12345678", "x", "x", 1L, null)))
                .isInstanceOf(OperacionInvalidaException.class)
                .hasMessageContaining("dup@x.com");
    }

    @Test
    void eliminar_es_baja_logica_setea_activo_false() {
        Usuario u = new Usuario();
        u.setId(7L);
        u.setActivo(true);
        when(usuarioRepo.findById(7L)).thenReturn(Optional.of(u));

        service.eliminar(7L);

        assertThat(u.getActivo()).isFalse();
        verify(usuarioRepo, never()).delete(any(Usuario.class));
    }
}
