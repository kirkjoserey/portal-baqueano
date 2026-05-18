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

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
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
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" richColors />
    </AuthProvider>
  );
}
