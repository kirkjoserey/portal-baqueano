package ar.com.baqueano.service;

import ar.com.baqueano.domain.Prospecto;
import ar.com.baqueano.dto.prospecto.ProspectoCreateDTO;
import ar.com.baqueano.dto.prospecto.ProspectoListItemDTO;
import ar.com.baqueano.dto.prospecto.ProspectoResponseDTO;
import ar.com.baqueano.dto.prospecto.ProspectoUpdateDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.mapper.ProspectoMapper;
import ar.com.baqueano.repository.ProspectoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProspectoServiceImpl implements ProspectoService {

    private final ProspectoRepository repo;
    private final ProspectoMapper mapper;

    @Override
    public Page<ProspectoListItemDTO> listar(Pageable pageable) {
        return repo.findByActivoTrue(pageable).map(mapper::toListItemDTO);
    }

    @Override
    public ProspectoResponseDTO obtener(Long id) {
        return mapper.toResponseDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public ProspectoResponseDTO crear(ProspectoCreateDTO dto) {
        if (repo.existsByEmailAndActivoTrue(dto.email())) {
            throw new OperacionInvalidaException(
                    "Ya existe un prospecto activo con el email: " + dto.email());
        }
        Prospecto entity = mapper.toEntity(dto);
        if (entity.getEstado() == null)  entity.setEstado(Prospecto.Estado.NUEVO);
        if (entity.getOrigen() == null)  entity.setOrigen(Prospecto.Origen.WEB);
        if (entity.getActivo() == null)  entity.setActivo(Boolean.TRUE);
        return mapper.toResponseDTO(repo.save(entity));
    }

    @Override
    @Transactional
    public ProspectoResponseDTO actualizar(Long id, ProspectoUpdateDTO dto) {
        Prospecto entity = obtenerEntidad(id);
        // Si cambió el email, verificar que no exista en otro prospecto activo
        if (!entity.getEmail().equalsIgnoreCase(dto.email())
                && repo.existsByEmailAndActivoTrue(dto.email())) {
            throw new OperacionInvalidaException(
                    "Ya existe un prospecto activo con el email: " + dto.email());
        }
        mapper.updateEntity(entity, dto);
        return mapper.toResponseDTO(entity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Prospecto entity = obtenerEntidad(id);
        // Baja logica: marca activo=false en lugar de borrar el registro
        entity.setActivo(Boolean.FALSE);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Prospecto obtenerEntidad(Long id) {
        return repo.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Prospecto no encontrado: id=" + id));
    }
}
