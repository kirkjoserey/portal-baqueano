package ar.com.baqueano.service;

import ar.com.baqueano.domain.Contacto;
import ar.com.baqueano.dto.contacto.ContactoCreateDTO;
import ar.com.baqueano.dto.contacto.ContactoListItemDTO;
import ar.com.baqueano.dto.contacto.ContactoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactoService {

    Page<ContactoListItemDTO> listar(Pageable pageable);

    ContactoResponseDTO obtener(Long id);

    /** Alta publica (sin auth). El ipOrigen lo provee el controller. */
    ContactoResponseDTO crear(ContactoCreateDTO dto, String ipOrigen);

    ContactoResponseDTO actualizarEstado(Long id, Contacto.Estado estado);

    void eliminar(Long id);

    /** Ultimos 5 contactos en orden descendente por fechaCreacion (dashboard). */
    List<ContactoListItemDTO> ultimos();
}
