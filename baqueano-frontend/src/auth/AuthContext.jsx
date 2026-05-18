import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import api, { tokenStore } from '../api/axios';

export const AuthContext = createContext(null);

const STORAGE_USER = 'baqueano.user';

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem(STORAGE_USER);
    return raw ? JSON.parse(raw) : null;
  });
  const [menu, setMenu] = useState([]);
  const [loadingMenu, setLoadingMenu] = useState(false);

  const persistUser = (u) => {
    if (u) {
      localStorage.setItem(STORAGE_USER, JSON.stringify(u));
    } else {
      localStorage.removeItem(STORAGE_USER);
    }
    setUser(u);
  };

  const fetchMenu = useCallback(async () => {
    if (!tokenStore.getAccess()) return;
    setLoadingMenu(true);
    try {
      const { data } = await api.get('/menu/mio');
      setMenu(data);
    } finally {
      setLoadingMenu(false);
    }
  }, []);

  // Si entramos con token persistido, recargamos el menu al mount
  useEffect(() => {
    if (user && tokenStore.getAccess()) {
      fetchMenu();
    }
  }, [user, fetchMenu]);

  const login = async (username, password) => {
    const { data } = await api.post('/auth/login', { username, password });
    tokenStore.setAccess(data.accessToken);
    tokenStore.setRefresh(data.refreshToken);
    persistUser({
      id: data.usuarioId,
      username: data.username,
      perfilId: data.perfilId,
      perfilNombre: data.perfilNombre,
    });
    await fetchMenu();
    return data;
  };

  const logout = async () => {
    const refresh = tokenStore.getRefresh();
    try {
      if (refresh) {
        await api.post('/auth/logout', { refreshToken: refresh });
      }
    } catch (_) {
      // ignoramos errores de logout - igual limpiamos local
    }
    tokenStore.clear();
    persistUser(null);
    setMenu([]);
  };

  // Helper: permisos para una ruta (lo usan los ABMs en Fase 8)
  const getPermisos = useCallback((ruta) => {
    const all = menu.flatMap((m) => m.submenus);
    return (
      all.find((s) => s.ruta === ruta) ?? {
        puedeVer: false,
        puedeCrear: false,
        puedeEditar: false,
        puedeEliminar: false,
      }
    );
  }, [menu]);

  const value = useMemo(
    () => ({ user, menu, loadingMenu, login, logout, getPermisos }),
    [user, menu, loadingMenu, getPermisos],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
