import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './useAuth.js';
import { tokenStore } from '../api/axios.js';

export default function ProtectedRoute() {
  const { user } = useAuth();
  const location = useLocation();

  if (!user || !tokenStore.getAccess()) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
