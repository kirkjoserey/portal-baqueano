package ar.com.baqueano.controller;

import ar.com.baqueano.dto.parametro.ParametroCreateDTO;
import ar.com.baqueano.dto.parametro.ParametroResponseDTO;
import ar.com.baqueano.dto.parametro.ParametroUpdateDTO;
import ar.com.baqueano.service.ParametroService;
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
@RequestMapping("/api/v1/parametros")
@RequiredArgsConstructor
public class ParametroController {

    private static final String RUTA = "/parametros";

    private final ParametroService service;

    @GetMapping
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<Page<ParametroResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<ParametroResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/clave/{clave}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'VER')")
    public ResponseEntity<ParametroResponseDTO> obtenerPorClave(@PathVariable String clave) {
        return ResponseEntity.ok(service.obtenerPorClave(clave));
    }

    @PostMapping
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'CREAR')")
    public ResponseEntity<ParametroResponseDTO> crear(@Valid @RequestBody ParametroCreateDTO dto,
                                                      UriComponentsBuilder uri) {
        ParametroResponseDTO out = service.crear(dto);
        URI location = uri.path("/api/v1/parametros/{id}").buildAndExpand(out.id()).toUri();
        return ResponseEntity.created(location).body(out);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'EDITAR')")
    public ResponseEntity<ParametroResponseDTO> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ParametroUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permisoEvaluator.puede(authentication, '" + RUTA + "', 'ELIMINAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
