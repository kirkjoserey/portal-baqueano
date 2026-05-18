package ar.com.baqueano.security;

import ar.com.baqueano.repository.SubmenuPerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bean usado desde @PreAuthorize. Ej:
 *   @PreAuthorize("@permisoEvaluator.puede(authentication, '/usuarios', 'CREAR')")
 *
 * Acciones validas: VER, CREAR, EDITAR, ELIMINAR.
 */
@Component("permisoEvaluator")
@RequiredArgsConstructor
public class PermisoEvaluator {

    private final SubmenuPerfilRepository spRepo;

    @Transactional(readOnly = true)
    public boolean puede(Authentication auth, String ruta, String accion) {
        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof BaqueanoUserDetails ud)) {
            return false;
        }
        return spRepo.findPermisos(ud.getPerfilId(), ruta)
                .map(sp -> switch (accion.toUpperCase()) {
                    case "VER" -> Boolean.TRUE.equals(sp.getPuedeVer());
                    case "CREAR" -> Boolean.TRUE.equals(sp.getPuedeCrear());
                    case "EDITAR" -> Boolean.TRUE.equals(sp.getPuedeEditar());
                    case "ELIMINAR" -> Boolean.TRUE.equals(sp.getPuedeEliminar());
                    default -> false;
                })
                .orElse(false);
    }
}
