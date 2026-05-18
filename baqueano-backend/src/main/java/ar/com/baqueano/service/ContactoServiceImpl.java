package ar.com.baqueano.service;

import ar.com.baqueano.domain.Contacto;
import ar.com.baqueano.dto.contacto.ContactoCreateDTO;
import ar.com.baqueano.dto.contacto.ContactoListItemDTO;
import ar.com.baqueano.dto.contacto.ContactoResponseDTO;
import ar.com.baqueano.mapper.ContactoMapper;
import ar.com.baqueano.repository.ContactoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactoServiceImpl implements ContactoService {

    private final ContactoRepository repo;
    private final ContactoMapper mapper;

    @Override
    public Page<ContactoListItemDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toListItemDTO);
    }

    @Override
    public ContactoResponseDTO obtener(Long id) {
        return mapper.toResponseDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public ContactoResponseDTO crear(ContactoCreateDTO dto, String ipOrigen) {
        Contacto entity = mapper.toEntity(dto, ipOrigen);
        return mapper.toResponseDTO(repo.save(entity));
    }

    @Override
    @Transactional
    public ContactoResponseDTO actualizarEstado(Long id, Contacto.Estado estado) {
        Contacto entity = obtenerEntidad(id);
        entity.setEstado(estado);
        return mapper.toResponseDTO(entity);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repo.delete(obtenerEntidad(id));
    }

    @Override
    public List<ContactoListItemDTO> ultimos() {
        return repo.findTop5ByOrderByFechaCreacionDesc().stream()
                .map(mapper::toListItemDTO)
                .toList();
    }

    private Contacto obtenerEntidad(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contacto no encontrado: id=" + id));
    }
}
