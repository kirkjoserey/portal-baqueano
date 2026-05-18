package ar.com.baqueano.service;

import ar.com.baqueano.domain.Parametro;
import ar.com.baqueano.dto.parametro.ParametroCreateDTO;
import ar.com.baqueano.dto.parametro.ParametroResponseDTO;
import ar.com.baqueano.dto.parametro.ParametroUpdateDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.mapper.ParametroMapper;
import ar.com.baqueano.repository.ParametroRepository;
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
class ParametroServiceImplTest {

    @Mock
    private ParametroRepository repo;

    private final ParametroMapper mapper = Mappers.getMapper(ParametroMapper.class);
    private ParametroServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ParametroServiceImpl(repo, mapper);
    }

    @Test
    void crear_persiste_y_retorna_dto() {
        when(repo.existsByClave("nuevo.param")).thenReturn(false);
        when(repo.save(any(Parametro.class))).thenAnswer(inv -> {
            Parametro p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ParametroResponseDTO out = service.crear(new ParametroCreateDTO(
                "nuevo.param", "valor", "desc", Parametro.TipoDato.STRING, null));

        assertThat(out.id()).isEqualTo(10L);
        assertThat(out.editable()).isTrue();
        assertThat(out.tipoDato()).isEqualTo(Parametro.TipoDato.STRING);
    }

    @Test
    void crear_falla_si_clave_duplicada() {
        when(repo.existsByClave("app.nombre")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(new ParametroCreateDTO(
                "app.nombre", "X", null, Parametro.TipoDato.STRING, true)))
                .isInstanceOf(OperacionInvalidaException.class);
    }

    @Test
    void actualizar_falla_si_parametro_no_es_editable() {
        Parametro fijo = new Parametro();
        fijo.setId(2L);
        fijo.setClave("fijo");
        fijo.setEditable(false);
        when(repo.findById(2L)).thenReturn(Optional.of(fijo));

        assertThatThrownBy(() -> service.actualizar(2L, new ParametroUpdateDTO("X", null, true)))
                .isInstanceOf(OperacionInvalidaException.class)
                .hasMessageContaining("no es editable");

        verify(repo, never()).save(any());
    }

    @Test
    void obtenerPorClave_lanza_EntityNotFound_si_no_existe() {
        when(repo.findByClave("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorClave("inexistente"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
