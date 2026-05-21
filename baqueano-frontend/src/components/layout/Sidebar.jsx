import { NavLink } from 'react-router-dom';
import { Zap, LogOut } from 'lucide-react';
import Icon from '../ui/Icon.jsx';
import { useAuth } from '../../auth/useAuth.js';

export default function Sidebar({ open, onClose }) {
  const { user, menu, logout } = useAuth();
  const initial = user?.username?.charAt(0).toUpperCase() ?? '?';

  return (
    /*
     * En mobile: posicion fija, se desliza desde la izquierda.
     *   - cerrado: -translate-x-full (fuera de pantalla)
     *   - abierto:  translate-x-0    (visible)
     * En md+: posicion estatica, siempre visible (translate-x-0 forzado via md:static).
     */
    <aside
      className={[
        'fixed inset-y-0 left-0 z-30 w-[260px] shrink-0',
        'bg-bg-sidebar text-text-on-dark flex flex-col',
        'transition-transform duration-200 ease-in-out',
        'md:static md:translate-x-0',
        open ? 'translate-x-0' : '-translate-x-full',
      ].join(' ')}
    >
      {/* Logo */}
      <div className="flex items-center gap-3 px-5 py-5">
        <div className="bg-primary rounded-md p-2 flex items-center justify-center">
          <Zap className="h-5 w-5 text-white" strokeWidth={2.5} />
        </div>
        <div>
          <div className="font-bold text-base leading-tight">Baqueano</div>
          <div className="text-text-muted-dark text-xs">Sistema de Gestión</div>
        </div>
      </div>

      {/* Menu dinamico */}
      <nav className="flex-1 overflow-y-auto px-3 mt-2">
        {menu.map((grupo) => (
          <div key={grupo.nombre} className="mb-4">
            <div className="text-text-muted-dark text-[11px] tracking-widest font-semibold px-3 py-2 uppercase">
              {grupo.nombre}
            </div>
            <ul className="space-y-1">
              {grupo.submenus
                .filter((s) => s.puedeVer)
                .map((s) => (
                  <li key={s.ruta}>
                    <NavLink
                      to={s.ruta}
                      onClick={onClose}   /* cierra el drawer al navegar en mobile */
                      className={({ isActive }) =>
                        [
                          'flex items-center gap-3 px-3 py-2.5 rounded-md text-sm transition-colors',
                          isActive
                            ? 'bg-primary text-white'
                            : 'text-text-muted-dark hover:bg-bg-sidebar-deep hover:text-white',
                        ].join(' ')
                      }
                    >
                      <Icon name={s.icono} className="h-[18px] w-[18px] shrink-0" />
                      <span>{s.nombre}</span>
                    </NavLink>
                  </li>
                ))}
            </ul>
          </div>
        ))}
      </nav>

      {/* Footer: user + logout */}
      <div className="bg-bg-sidebar-deep px-4 py-4">
        <div className="flex items-center gap-3 mb-3">
          <div className="bg-primary text-white h-9 w-9 rounded-full flex items-center justify-center font-semibold shrink-0">
            {initial}
          </div>
          <div className="text-sm min-w-0">
            <div className="font-medium truncate">{user?.username}</div>
            <div className="text-text-muted-dark text-xs truncate">{user?.perfilNombre}</div>
          </div>
        </div>
        <button
          type="button"
          onClick={logout}
          className="w-full flex items-center gap-2 px-3 py-2.5 rounded-md text-sm text-text-muted-dark hover:bg-bg-sidebar hover:text-white transition-colors"
        >
          <LogOut className="h-4 w-4 shrink-0" />
          Cerrar Sesión
        </button>
      </div>
    </aside>
  );
}
