package ar.com.baqueano.repository;

import ar.com.baqueano.domain.SubmenuPerfil;
import ar.com.baqueano.domain.SubmenuPerfilId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmenuPerfilRepository extends JpaRepository<SubmenuPerfil, SubmenuPerfilId> {

    List<SubmenuPerfil> findByPerfilId(Long perfilId);

    /**
     * Permisos del perfil indicado para una ruta. Lo usa el bean
     * permisoEvaluator que cablearemos en Fase 5 (@PreAuthorize).
     */
    @Query("""
            SELECT sp FROM SubmenuPerfil sp
            WHERE sp.perfil.id = :perfilId
              AND sp.submenu.ruta = :ruta
            """)
    Optional<SubmenuPerfil> findPermisos(@Param("perfilId") Long perfilId,
                                        @Param("ruta") String ruta);
}
