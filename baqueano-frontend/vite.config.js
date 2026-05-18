import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev: localhost:5173, base '/'. El backend corre en 8081 (Fase 2),
// proxiamos cualquier ruta /baqueano para evitar CORS local.
//
// Prod: base '/baqueano/' para que los assets generados apunten a
// /baqueano/assets/... (que es la ruta donde WildFly serviria la app
// con el WAR baqueano.war). React Router usa import.meta.env.BASE_URL
// como basename para que las rutas internas tambien se prefijen.
export default defineConfig(({ mode }) => ({
  plugins: [react()],
  base: mode === 'production' ? '/baqueano/' : '/',
  server: {
    port: 5173,
    proxy: {
      '/baqueano': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    emptyOutDir: true,
  },
}));
