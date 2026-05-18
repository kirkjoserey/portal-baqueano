import axios from 'axios';

const STORAGE_ACCESS = 'baqueano.access';
const STORAGE_REFRESH = 'baqueano.refresh';

export const tokenStore = {
  getAccess: () => localStorage.getItem(STORAGE_ACCESS),
  setAccess: (t) => localStorage.setItem(STORAGE_ACCESS, t),
  getRefresh: () => localStorage.getItem(STORAGE_REFRESH),
  setRefresh: (t) => localStorage.setItem(STORAGE_REFRESH, t),
  clear: () => {
    localStorage.removeItem(STORAGE_ACCESS);
    localStorage.removeItem(STORAGE_REFRESH);
  },
};

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/baqueano/api/v1',
});

api.interceptors.request.use((config) => {
  const token = tokenStore.getAccess();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshing = null;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const status = error.response?.status;

    // Solo intentamos refresh una vez por request, y nunca contra el propio /auth/refresh
    const isAuthEndpoint = original?.url?.startsWith('/auth/');
    if (status === 401 && !original._retry && !isAuthEndpoint && tokenStore.getRefresh()) {
      original._retry = true;
      try {
        refreshing = refreshing || axios.post(
          (import.meta.env.VITE_API_URL || '/baqueano/api/v1') + '/auth/refresh',
          { refreshToken: tokenStore.getRefresh() },
        );
        const res = await refreshing;
        refreshing = null;
        tokenStore.setAccess(res.data.accessToken);
        original.headers.Authorization = `Bearer ${res.data.accessToken}`;
        return api(original);
      } catch (refreshError) {
        refreshing = null;
        tokenStore.clear();
        if (typeof window !== 'undefined') {
          // BASE_URL incluye el contexto del WAR en prod (/baqueano/)
          window.location.href = (import.meta.env.BASE_URL || '/') + 'login';
        }
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
