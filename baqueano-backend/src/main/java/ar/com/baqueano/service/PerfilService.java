package ar.com.baqueano.service;

import ar.com.baqueano.dto.perfil.PerfilCreateDTO;
import ar.com.baqueano.dto.perfil.PerfilResponseDTO;
import ar.com.baqueano.dto.perfil.PerfilUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerfilService {

    Page<PerfilResponseDTO> listar(Pageable pageable);

    PerfilResponseDTO obtener(Long id);

    PerfilResponseDTO crear(PerfilCreateDTO dto);

    PerfilResponseDTO actualizar(Long id, PerfilUpdateDTO dto);

    void eliminar(Long id);
}
