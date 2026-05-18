package ar.com.baqueano.controller;

import ar.com.baqueano.dto.contacto.ContactoCreateDTO;
import ar.com.baqueano.dto.contacto.ContactoListItemDTO;
import ar.com.baqueano.dto.contacto.ContactoResponseDTO;
import ar.com.baqueano.dto.contacto.ContactoUpdateEstadoDTO;
import ar.com.baqueano.service.ContactoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/contactos")
@RequiredArgsConstructor
public class ContactoController {

    private static final String RUTA = "/contactos";

    private final ContactoService service;

    /** Alta publica: no requiere autenticacion. Captura la IP del cliente. */
    @PostMapping
    public ResponseEntity<ContactoResponseDTO> crear(@Valid @RequestBody ContactoCreateDTO dto,
                                                     HttpServletRequest request,
                                                     UriComponentsBuilder uri) {
        ContactoResponseDTO out = service.crear(dto, request.getRemoteAddr());
        URI location = uri.path("/api/v1/contactos/{id}").buildAndExpand(out.id()).toUri();
        return ResponseEntity.created(location).body(out);
    }

    @GetMapping
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<Page<ContactoListItemDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<ContactoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'EDITAR')")
    public ResponseEntity<ContactoResponseDTO> actualizarEstado(@PathVariable Long id,
                                                                @Valid @RequestBody ContactoUpdateEstadoDTO dto) {
        return ResponseEntity.ok(service.actualizarEstado(id, dto.estado()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
