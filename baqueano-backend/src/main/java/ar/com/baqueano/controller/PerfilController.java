package ar.com.baqueano.controller;

import ar.com.baqueano.dto.perfil.PerfilCreateDTO;
import ar.com.baqueano.dto.perfil.PerfilResponseDTO;
import ar.com.baqueano.dto.perfil.PerfilUpdateDTO;
import ar.com.baqueano.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/perfiles")
@RequiredArgsConstructor
public class PerfilController {

    private static final String RUTA = "/perfiles";

    private final PerfilService service;

    @GetMapping
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<Page<PerfilResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<PerfilResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'CREAR')")
    public ResponseEntity<PerfilResponseDTO> crear(@Valid @RequestBody PerfilCreateDTO dto,
                                                   UriComponentsBuilder uri) {
        PerfilResponseDTO out = service.crear(dto);
        URI location = uri.path("/api/v1/perfiles/{id}").buildAndExpand(out.id()).toUri();
        return ResponseEntity.created(location).body(out);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'EDITAR')")
    public ResponseEntity<PerfilResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody PerfilUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
