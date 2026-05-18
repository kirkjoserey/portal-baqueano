import { useCallback, useEffect, useRef, useState } from 'react';
import api from '../api/axios.js';

/**
 * Hook para endpoints que devuelven Page<T> de Spring.
 * Tira el GET cuando cambia url, page, size, sort o filters.
 * Reload manual con `reload()`.
 */
export default function usePagedQuery(url, { page = 0, size = 10, sort, filters } = {}) {
  const [data, setData] = useState({ content: [], totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [reloadCounter, setReloadCounter] = useState(0);

  // Serializamos filters para que useEffect detecte cambios
  const filtersKey = JSON.stringify(filters ?? {});
  const filtersRef = useRef(filters);
  filtersRef.current = filters;

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);

    const params = { page, size, ...(filtersRef.current ?? {}) };
    if (sort) params.sort = sort;

    api.get(url, { params })
      .then((res) => {
        if (active) setData(res.data);
      })
      .catch((err) => {
        if (active) setError(err);
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => { active = false; };
  }, [url, page, size, sort, filtersKey, reloadCounter]);

  const reload = useCallback(() => setReloadCounter((c) => c + 1), []);

  return { ...data, loading, error, reload };
}
