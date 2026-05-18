package ar.com.baqueano.service;

import ar.com.baqueano.dto.parametro.ParametroCreateDTO;
import ar.com.baqueano.dto.parametro.ParametroResponseDTO;
import ar.com.baqueano.dto.parametro.ParametroUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParametroService {

    Page<ParametroResponseDTO> listar(Pageable pageable);

    ParametroResponseDTO obtener(Long id);

    ParametroResponseDTO obtenerPorClave(String clave);

    ParametroResponseDTO crear(ParametroCreateDTO dto);

    ParametroResponseDTO actualizar(Long id, ParametroUpdateDTO dto);

    void eliminar(Long id);
}
