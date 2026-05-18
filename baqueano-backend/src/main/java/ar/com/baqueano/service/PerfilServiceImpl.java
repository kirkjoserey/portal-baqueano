package ar.com.baqueano.service;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.dto.perfil.PerfilCreateDTO;
import ar.com.baqueano.dto.perfil.PerfilResponseDTO;
import ar.com.baqueano.dto.perfil.PerfilUpdateDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.mapper.PerfilMapper;
import ar.com.baqueano.repository.PerfilRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerfilServiceImpl implements PerfilService {

    private final PerfilRepository perfilRepo;
    private final UsuarioRepository usuarioRepo;
    private final PerfilMapper mapper;

    @Override
    public Page<PerfilResponseDTO> listar(Pageable pageable) {
        return perfilRepo.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    public PerfilResponseDTO obtener(Long id) {
        return mapper.toResponseDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public PerfilResponseDTO crear(PerfilCreateDTO dto) {
        if (perfilRepo.existsByNombre(dto.nombre())) {
            throw new OperacionInvalidaException("Ya existe un perfil con nombre: " + dto.nombre());
        }
        Perfil entity = mapper.toEntity(dto);
        if (entity.getActivo() == null) {
            entity.setActivo(Boolean.TRUE);
        }
        return mapper.toResponseDTO(perfilRepo.save(entity));
    }

    @Override
    @Transactional
    public PerfilResponseDTO actualizar(Long id, PerfilUpdateDTO dto) {
        Perfil entity = obtenerEntidad(id);
        if (!entity.getNombre().equals(dto.nombre()) && perfilRepo.existsByNombre(dto.nombre())) {
            throw new OperacionInvalidaException("Ya existe un perfil con nombre: " + dto.nombre());
        }
        mapper.updateEntity(entity, dto);
        return mapper.toResponseDTO(entity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Perfil entity = obtenerEntidad(id);
        if (usuarioRepo.countByPerfilId(id) > 0) {
            throw new OperacionInvalidaException(
                    "No se puede eliminar el perfil porque tiene usuarios asociados");
        }
        perfilRepo.delete(entity);
    }

    private Perfil obtenerEntidad(Long id) {
        return perfilRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado: id=" + id));
    }
}
