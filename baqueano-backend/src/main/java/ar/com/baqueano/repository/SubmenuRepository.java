package ar.com.baqueano.repository;

import ar.com.baqueano.domain.Submenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmenuRepository extends JpaRepository<Submenu, Long> {

    Optional<Submenu> findByRuta(String ruta);

    /**
     * Devuelve los submenus visibles para un perfil (puedeVer=true y submenu+menu activos),
     * ordenados por menu.orden, submenu.orden. Base del menu dinamico (spec seccion 5.3).
     */
    @Query("""
            SELECT s FROM Submenu s
            JOIN s.menu m
            JOIN SubmenuPerfil sp ON sp.submenu = s
            WHERE sp.perfil.id = :perfilId
              AND sp.puedeVer = true
              AND s.activo = true
              AND m.activo = true
            ORDER BY m.orden, s.orden
            """)
    List<Submenu> findMenuPorPerfil(@Param("perfilId") Long perfilId);
}
