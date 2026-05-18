package ar.com.baqueano.service;

import ar.com.baqueano.domain.Contacto;
import ar.com.baqueano.dto.contacto.ContactoCreateDTO;
import ar.com.baqueano.dto.contacto.ContactoResponseDTO;
import ar.com.baqueano.mapper.ContactoMapper;
import ar.com.baqueano.repository.ContactoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactoServiceImplTest {

    @Mock
    private ContactoRepository repo;

    private final ContactoMapper mapper = Mappers.getMapper(ContactoMapper.class);
    private ContactoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ContactoServiceImpl(repo, mapper);
    }

    @Test
    void crear_guarda_ip_origen_y_estado_NUEVO_por_defecto() {
        when(repo.save(any(Contacto.class))).thenAnswer(inv -> {
            Contacto c = inv.getArgument(0);
            c.setId(11L);
            return c;
        });

        ContactoResponseDTO out = service.crear(
                new ContactoCreateDTO("Pepe", "p@x.com", "11111", "Hola", "Mensaje de prueba"),
                "127.0.0.1");

        ArgumentCaptor<Contacto> cap = ArgumentCaptor.forClass(Contacto.class);
        verify(repo).save(cap.capture());
        assertThat(cap.getValue().getIpOrigen()).isEqualTo("127.0.0.1");
        assertThat(cap.getValue().getEstado()).isEqualTo(Contacto.Estado.NUEVO);

        assertThat(out.id()).isEqualTo(11L);
        assertThat(out.estado()).isEqualTo(Contacto.Estado.NUEVO);
    }

    @Test
    void actualizarEstado_modifica_el_estado_del_contacto() {
        Contacto c = new Contacto();
        c.setId(3L);
        c.setEstado(Contacto.Estado.NUEVO);
        when(repo.findById(3L)).thenReturn(Optional.of(c));

        ContactoResponseDTO out = service.actualizarEstado(3L, Contacto.Estado.RESPONDIDO);

        assertThat(c.getEstado()).isEqualTo(Contacto.Estado.RESPONDIDO);
        assertThat(out.estado()).isEqualTo(Contacto.Estado.RESPONDIDO);
    }
}
