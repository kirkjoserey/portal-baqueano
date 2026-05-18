package ar.com.baqueano.controller;

import ar.com.baqueano.dto.usuario.UsuarioCreateDTO;
import ar.com.baqueano.dto.usuario.UsuarioListItemDTO;
import ar.com.baqueano.dto.usuario.UsuarioResponseDTO;
import ar.com.baqueano.dto.usuario.UsuarioUpdateDTO;
import ar.com.baqueano.service.UsuarioService;
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
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private static final String RUTA = "/usuarios";

    private final UsuarioService service;

    @GetMapping
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<Page<UsuarioListItemDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'CREAR')")
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioCreateDTO dto,
                                                    UriComponentsBuilder uri) {
        UsuarioResponseDTO out = service.crear(dto);
        URI location = uri.path("/api/v1/usuarios/{id}").buildAndExpand(out.id()).toUri();
        return ResponseEntity.created(location).body(out);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'EDITAR')")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
