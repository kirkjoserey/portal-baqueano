package ar.com.baqueano.service;

import ar.com.baqueano.dto.usuario.UsuarioCreateDTO;
import ar.com.baqueano.dto.usuario.UsuarioListItemDTO;
import ar.com.baqueano.dto.usuario.UsuarioResponseDTO;
import ar.com.baqueano.dto.usuario.UsuarioUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    Page<UsuarioListItemDTO> listar(Pageable pageable);

    UsuarioResponseDTO obtener(Long id);

    UsuarioResponseDTO crear(UsuarioCreateDTO dto);

    UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO dto);

    /** Baja logica: marca activo=false, no elimina la fila. */
    void eliminar(Long id);
}
