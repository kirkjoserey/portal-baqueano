package ar.com.baqueano.service;

import ar.com.baqueano.dto.prospecto.ProspectoCreateDTO;
import ar.com.baqueano.dto.prospecto.ProspectoListItemDTO;
import ar.com.baqueano.dto.prospecto.ProspectoResponseDTO;
import ar.com.baqueano.dto.prospecto.ProspectoUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProspectoService {

    Page<ProspectoListItemDTO> listar(Pageable pageable);

    ProspectoResponseDTO obtener(Long id);

    ProspectoResponseDTO crear(ProspectoCreateDTO dto);

    ProspectoResponseDTO actualizar(Long id, ProspectoUpdateDTO dto);

    void eliminar(Long id);
}
