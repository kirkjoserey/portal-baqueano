package ar.com.baqueano.service;

import ar.com.baqueano.domain.Perfil;
import ar.com.baqueano.domain.Usuario;
import ar.com.baqueano.dto.usuario.UsuarioCreateDTO;
import ar.com.baqueano.dto.usuario.UsuarioListItemDTO;
import ar.com.baqueano.dto.usuario.UsuarioResponseDTO;
import ar.com.baqueano.dto.usuario.UsuarioUpdateDTO;
import ar.com.baqueano.exception.OperacionInvalidaException;
import ar.com.baqueano.mapper.UsuarioMapper;
import ar.com.baqueano.repository.PerfilRepository;
import ar.com.baqueano.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final PerfilRepository perfilRepo;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UsuarioListItemDTO> listar(Pageable pageable) {
        return usuarioRepo.findAll(pageable).map(mapper::toListItemDTO);
    }

    @Override
    public UsuarioResponseDTO obtener(Long id) {
        return mapper.toResponseDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioCreateDTO dto) {
        if (usuarioRepo.existsByUsername(dto.username())) {
            throw new OperacionInvalidaException("Username ya existe: " + dto.username());
        }
        if (usuarioRepo.existsByEmail(dto.email())) {
            throw new OperacionInvalidaException("Email ya existe: " + dto.email());
        }
        Perfil perfil = obtenerPerfil(dto.perfilId());

        Usuario u = mapper.toEntity(dto, perfil);
        u.setPasswordHash(passwordEncoder.encode(dto.password()));
        if (u.getActivo() == null) {
            u.setActivo(Boolean.TRUE);
        }
        if (u.getIntentosFallidos() == null) {
            u.setIntentosFallidos(0);
        }
        return mapper.toResponseDTO(usuarioRepo.save(u));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario u = obtenerEntidad(id);
        if (!u.getEmail().equals(dto.email()) && usuarioRepo.existsByEmail(dto.email())) {
            throw new OperacionInvalidaException("Email ya existe: " + dto.email());
        }
        Perfil perfil = obtenerPerfil(dto.perfilId());
        mapper.updateEntity(u, dto, perfil);
        return mapper.toResponseDTO(u);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario u = obtenerEntidad(id);
        u.setActivo(Boolean.FALSE);
    }

    private Usuario obtenerEntidad(Long id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: id=" + id));
    }

    private Perfil obtenerPerfil(Long perfilId) {
        return perfilRepo.findById(perfilId)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado: id=" + perfilId));
    }
}
