package ar.com.baqueano.service;

import ar.com.baqueano.domain.Menu;
import ar.com.baqueano.domain.Submenu;
import ar.com.baqueano.domain.SubmenuPerfil;
import ar.com.baqueano.dto.menu.MenuItemDTO;
import ar.com.baqueano.dto.menu.SubmenuItemDTO;
import ar.com.baqueano.repository.SubmenuPerfilRepository;
import ar.com.baqueano.repository.SubmenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private final SubmenuRepository submenuRepo;
    private final SubmenuPerfilRepository spRepo;

    @Override
    public List<MenuItemDTO> obtenerMenuPara(Long perfilId) {
        List<Submenu> submenus = submenuRepo.findMenuPorPerfil(perfilId);

        Map<Long, SubmenuPerfil> permisosPorSubmenu = spRepo.findByPerfilId(perfilId).stream()
                .collect(Collectors.toMap(
                        sp -> sp.getSubmenu().getId(),
                        sp -> sp));

        Map<Long, MenuItemDTO> menus = new LinkedHashMap<>();
        for (Submenu s : submenus) {
            Menu m = s.getMenu();
            MenuItemDTO menuItem = menus.computeIfAbsent(m.getId(),
                    k -> new MenuItemDTO(m.getNombre(), m.getIcono(), m.getOrden(), new ArrayList<>()));

            SubmenuPerfil sp = permisosPorSubmenu.get(s.getId());
            menuItem.submenus().add(new SubmenuItemDTO(
                    s.getNombre(),
                    s.getRuta(),
                    s.getIcono(),
                    s.getOrden(),
                    sp != null && Boolean.TRUE.equals(sp.getPuedeVer()),
                    sp != null && Boolean.TRUE.equals(sp.getPuedeCrear()),
                    sp != null && Boolean.TRUE.equals(sp.getPuedeEditar()),
                    sp != null && Boolean.TRUE.equals(sp.getPuedeEliminar())
            ));
        }

        return new ArrayList<>(menus.values());
    }
}
