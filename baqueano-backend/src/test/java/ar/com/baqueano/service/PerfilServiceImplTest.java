package ar.com.baqueano.service;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.dto.perfil.PerfilCreateDTO;
import ar.com.baqueano.dto.perfil.PerfilResponseDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.mapper.PerfilMapper;
import ar.com.baqueano.repository.PerfilRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilServiceImplTest {

    @Mock
    private PerfilRepository perfilRepo;
    @Mock
    private UsuarioRepository usuarioRepo;

    private final PerfilMapper mapper = Mappers.getMapper(PerfilMapper.class);
    private PerfilServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PerfilServiceImpl(perfilRepo, usuarioRepo, mapper);
    }

    @Test
    void crear_devuelve_dto_y_aplica_activo_por_defecto_TRUE_cuando_es_null() {
        when(perfilRepo.existsByNombre("PRUEBA")).thenReturn(false);
        when(perfilRepo.save(any(Perfil.class))).thenAnswer(inv -> {
            Perfil p = inv.getArgument(0);
            p.setId(99L);
            return p;
        });

        PerfilResponseDTO out = service.crear(new PerfilCreateDTO("PRUEBA", "desc", null));

        assertThat(out.id()).isEqualTo(99L);
        assertThat(out.nombre()).isEqualTo("PRUEBA");
        assertThat(out.activo()).isTrue();
    }

    @Test
    void crear_falla_si_nombre_duplicado() {
        when(perfilRepo.existsByNombre("ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(new PerfilCreateDTO("ADMIN", "x", true)))
                .isInstanceOf(OperacionInvalidaException.class)
                .hasMessageContaining("ADMIN");

        verify(perfilRepo, never()).save(any());
    }

    @Test
    void eliminar_falla_si_perfil_tiene_usuarios() {
        Perfil p = new Perfil();
        p.setId(1L);
        when(perfilRepo.findById(1L)).thenReturn(Optional.of(p));
        when(usuarioRepo.countByPerfilId(1L)).thenReturn(3L);

        assertThatThrownBy(() -> service.eliminar(1L))
                .isInstanceOf(OperacionInvalidaException.class)
                .hasMessageContaining("usuarios asociados");

        verify(perfilRepo, never()).delete(any());
    }

    @Test
    void eliminar_funciona_sin_usuarios_asociados() {
        Perfil p = new Perfil();
        p.setId(5L);
        when(perfilRepo.findById(5L)).thenReturn(Optional.of(p));
        when(usuarioRepo.countByPerfilId(5L)).thenReturn(0L);

        service.eliminar(5L);

        verify(perfilRepo).delete(p);
    }

    @Test
    void obtener_lanza_EntityNotFoundException_si_no_existe() {
        when(perfilRepo.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(404L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
