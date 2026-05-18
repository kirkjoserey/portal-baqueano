package ar.com.baqueano.service;

import ar.com.baqueano.domain.Parametro;
import ar.com.baqueano.dto.parametro.ParametroCreateDTO;
import ar.com.baqueano.dto.parametro.ParametroResponseDTO;
import ar.com.baqueano.dto.parametro.ParametroUpdateDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.mapper.ParametroMapper;
import ar.com.baqueano.repository.ParametroRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParametroServiceImpl implements ParametroService {

    private final ParametroRepository repo;
    private final ParametroMapper mapper;

    @Override
    public Page<ParametroResponseDTO> listar(Pageable pageable) {
        return repo.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    public ParametroResponseDTO obtener(Long id) {
        return mapper.toResponseDTO(obtenerEntidad(id));
    }

    @Override
    @Cacheable(value = "parametros", key = "#clave")
    public ParametroResponseDTO obtenerPorClave(String clave) {
        return repo.findByClave(clave)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Parametro no encontrado: clave=" + clave));
    }

    @Override
    @Transactional
    @CacheEvict(value = "parametros", allEntries = true)
    public ParametroResponseDTO crear(ParametroCreateDTO dto) {
        if (repo.existsByClave(dto.clave())) {
            throw new OperacionInvalidaException("Ya existe un parametro con clave: " + dto.clave());
        }
        Parametro entity = mapper.toEntity(dto);
        if (entity.getEditable() == null) entity.setEditable(Boolean.TRUE);
        if (entity.getTipoDato() == null) entity.setTipoDato(Parametro.TipoDato.STRING);
        return mapper.toResponseDTO(repo.save(entity));
    }

    @Override
    @Transactional
    @CacheEvict(value = "parametros", allEntries = true)
    public ParametroResponseDTO actualizar(Long id, ParametroUpdateDTO dto) {
        Parametro entity = obtenerEntidad(id);
        if (Boolean.FALSE.equals(entity.getEditable())) {
            throw new OperacionInvalidaException("El parametro no es editable: " + entity.getClave());
        }
        mapper.updateEntity(entity, dto);
        return mapper.toResponseDTO(entity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "parametros", allEntries = true)
    public void eliminar(Long id) {
        repo.delete(obtenerEntidad(id));
    }

    private Parametro obtenerEntidad(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parametro no encontrado: id=" + id));
    }
}
