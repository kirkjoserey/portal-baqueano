import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev: localhost:5173. El backend corre en 8081 (Fase 2),
// proxiamos cualquier ruta /baqueano para evitar CORS local.
export default defineConfig({
  plugins: [react()],
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
});
