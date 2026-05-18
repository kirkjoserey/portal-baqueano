package ar.com.baqueano.service;

import ar.com.baqueano.domain.Contacto;
import ar.com.baqueano.dto.dashboard.DashboardResumenDTO;
import ar.com.baqueano.mapper.ContactoMapper;
import ar.com.baqueano.repository.ContactoRepository;
import ar.com.baqueano.repository.PerfilRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepo;
    @Mock
    private PerfilRepository perfilRepo;
    @Mock
    private ContactoRepository contactoRepo;

    private final ContactoMapper contactoMapper = Mappers.getMapper(ContactoMapper.class);
    private DashboardServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DashboardServiceImpl(usuarioRepo, perfilRepo, contactoRepo, contactoMapper);
    }

    @Test
    void obtenerResumen_agrega_totales_y_ultimos_contactos() {
        when(usuarioRepo.count()).thenReturn(7L);
        when(perfilRepo.count()).thenReturn(3L);
        when(contactoRepo.count()).thenReturn(15L);
        when(contactoRepo.countByEstado(Contacto.Estado.NUEVO)).thenReturn(4L);

        Contacto c1 = new Contacto();
        c1.setId(1L);
        c1.setNombre("Pepe");
        c1.setEmail("p@x.com");
        c1.setEstado(Contacto.Estado.NUEVO);
        when(contactoRepo.findTop5ByOrderByFechaCreacionDesc()).thenReturn(List.of(c1));

        DashboardResumenDTO r = service.obtenerResumen();

        assertThat(r.totalUsuarios()).isEqualTo(7L);
        assertThat(r.totalPerfiles()).isEqualTo(3L);
        assertThat(r.totalContactos()).isEqualTo(15L);
        assertThat(r.contactosNuevos()).isEqualTo(4L);
        assertThat(r.ultimosContactos()).hasSize(1);
        assertThat(r.ultimosContactos().get(0).nombre()).isEqualTo("Pepe");
    }
}
