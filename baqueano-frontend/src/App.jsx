import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from './auth/AuthContext.jsx';
import ProtectedRoute from './auth/ProtectedRoute.jsx';
import Layout from './components/layout/Layout.jsx';
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import UsuariosPage from './pages/usuarios/UsuariosPage.jsx';
import PerfilesPage from './pages/perfiles/PerfilesPage.jsx';
import ParametrosPage from './pages/parametros/ParametrosPage.jsx';
import ContactosPage from './pages/contactos/ContactosPage.jsx';
import ProspectosPage from './pages/prospectos/ProspectosPage.jsx';

// basename: en dev BASE_URL = '/'; en prod (vite build) = '/baqueano/'.
// React Router quiere basename sin barra final.
const BASENAME = (import.meta.env.BASE_URL || '/').replace(/\/$/, '') || '/';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter basename={BASENAME}>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/usuarios" element={<UsuariosPage />} />
              <Route path="/perfiles" element={<PerfilesPage />} />
              <Route path="/parametros" element={<ParametrosPage />} />
              <Route path="/contactos" element={<ContactosPage />} />
              <Route path="/prospectos" element={<ProspectosPage />} />
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" richColors />
    </AuthProvider>
  );
}
