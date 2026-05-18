import { useEffect, useState } from 'react';
import api from '../api/axios.js';

/**
 * Carga la lista completa de perfiles para selects (cantidad acotada).
 * Sin paginacion porque el dominio del problema tiene pocos perfiles.
 */
export default function usePerfilesAll() {
  const [perfiles, setPerfiles] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    api.get('/perfiles', { params: { size: 100, sort: 'nombre,asc' } })
      .then((res) => {
        if (active) setPerfiles(res.data.content ?? []);
      })
      .catch(() => {})
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, []);

  return { perfiles, loading };
}
