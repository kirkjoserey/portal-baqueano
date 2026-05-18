import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Zap } from 'lucide-react';
import { useAuth } from '../auth/useAuth.js';

const schema = z.object({
  username: z.string().min(1, 'Username requerido'),
  password: z.string().min(1, 'Password requerido'),
});

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [serverError, setServerError] = useState(null);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ resolver: zodResolver(schema) });

  const onSubmit = async ({ username, password }) => {
    setServerError(null);
    setLoading(true);
    try {
      await login(username, password);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      const code = err.response?.data?.code;
      const message = err.response?.data?.message ?? 'Error al iniciar sesión';
      setServerError(code ? `${message} (${code})` : message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-bg-app px-4">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-lg shadow-card p-8">
          <div className="flex flex-col items-center mb-8">
            <div className="bg-primary rounded-md p-3 mb-3">
              <Zap className="h-7 w-7 text-white" strokeWidth={2.5} />
            </div>
            <h1 className="text-2xl font-bold text-text-strong">Baqueano</h1>
            <p className="text-text-muted text-sm">Sistema de Gestión</p>
          </div>

          {serverError && (
            <div className="mb-4 px-4 py-3 rounded-md bg-badge-new-bg text-badge-new-text text-sm">
              {serverError}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-text-strong mb-1">
                Usuario
              </label>
              <input
                id="username"
                type="text"
                autoComplete="username"
                {...register('username')}
                className="w-full rounded-md border-border-soft shadow-sm focus:border-primary focus:ring-primary"
              />
              {errors.username && (
                <p className="mt-1 text-xs text-primary">{errors.username.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-text-strong mb-1">
                Contraseña
              </label>
              <input
                id="password"
                type="password"
                autoComplete="current-password"
                {...register('password')}
                className="w-full rounded-md border-border-soft shadow-sm focus:border-primary focus:ring-primary"
              />
              {errors.password && (
                <p className="mt-1 text-xs text-primary">{errors.password.message}</p>
              )}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary hover:bg-primary-hover text-white font-medium py-2.5 rounded-md transition-colors disabled:opacity-60"
            >
              {loading ? 'Ingresando...' : 'Ingresar'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
