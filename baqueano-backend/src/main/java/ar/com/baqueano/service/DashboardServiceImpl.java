package ar.com.baqueano.service;

import ar.com.baqueano.domain.Contacto;
import ar.com.baqueano.dto.contacto.ContactoListItemDTO;
import ar.com.baqueano.dto.dashboard.DashboardResumenDTO;
import ar.com.baqueano.mapper.ContactoMapper;
import ar.com.baqueano.repository.ContactoRepository;
import ar.com.baqueano.repository.PerfilRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UsuarioRepository usuarioRepo;
    private final PerfilRepository perfilRepo;
    private final ContactoRepository contactoRepo;
    private final ContactoMapper contactoMapper;

    @Override
    public DashboardResumenDTO obtenerResumen() {
        long totalUsuarios = usuarioRepo.count();
        long totalPerfiles = perfilRepo.count();
        long totalContactos = contactoRepo.count();
        long contactosNuevos = contactoRepo.countByEstado(Contacto.Estado.NUEVO);

        List<ContactoListItemDTO> ultimos = contactoRepo.findTop5ByOrderByFechaCreacionDesc().stream()
                .map(contactoMapper::toListItemDTO)
                .toList();

        return new DashboardResumenDTO(
                totalUsuarios,
                totalPerfiles,
                totalContactos,
                contactosNuevos,
                ultimos);
    }
}
