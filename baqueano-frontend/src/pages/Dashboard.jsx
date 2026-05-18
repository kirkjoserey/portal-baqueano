import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Users, Mail, Shield, Plus } from 'lucide-react';
import { toast } from 'sonner';
import api from '../api/axios.js';
import { useAuth } from '../auth/useAuth.js';

const HOY_FECHA_ES = new Intl.DateTimeFormat('es-AR', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
  year: 'numeric',
}).format(new Date());

function StatCard({ titulo, valor, descripcion, accentBg, accentText, IconCmp }) {
  return (
    <div className="bg-white rounded-lg shadow-card p-6 flex items-center justify-between">
      <div>
        <div className="text-xs uppercase tracking-widest text-text-muted font-semibold">{titulo}</div>
        <div className="text-3xl font-bold text-text-strong mt-2">{valor}</div>
        <div className="text-xs text-text-muted mt-1">{descripcion}</div>
      </div>
      <div className={`${accentBg} ${accentText} rounded-md p-3`}>
        <IconCmp className="h-6 w-6" />
      </div>
    </div>
  );
}

function ActionCard({ titulo, descripcion, ruta, color, IconCmp }) {
  return (
    <div className="bg-white rounded-lg shadow-card p-6 flex flex-col">
      <div className="flex items-start justify-between mb-3">
        <div className={`${color.bg} ${color.text} rounded-md p-2.5`}>
          <IconCmp className="h-5 w-5" />
        </div>
        <Link
          to={ruta}
          className={`${color.bg} ${color.text} hover:opacity-80 p-2 rounded-md transition-opacity`}
          aria-label={`Alta rápida en ${titulo}`}
        >
          <Plus className="h-4 w-4" />
        </Link>
      </div>
      <h3 className="font-semibold text-text-strong">{titulo}</h3>
      <p className="text-text-muted text-sm mt-1 mb-4 flex-1">{descripcion}</p>
      <Link
        to={ruta}
        className={`${color.btnBg} ${color.btnHover} text-white text-sm font-medium py-2 px-4 rounded-md text-center transition-colors`}
      >
        Ir a {titulo}
      </Link>
    </div>
  );
}

function EstadoBadge({ estado }) {
  const colorByEstado = {
    NUEVO: 'bg-badge-new-bg text-badge-new-text',
    LEIDO: 'bg-badge-read-bg text-badge-read-text',
    RESPONDIDO: 'bg-emerald-50 text-success',
    ARCHIVADO: 'bg-slate-100 text-slate-600',
  };
  return (
    <span className={`${colorByEstado[estado] ?? 'bg-badge-read-bg text-badge-read-text'} px-2 py-1 rounded-md text-xs font-semibold`}>
      {estado}
    </span>
  );
}

export default function Dashboard() {
  const { user } = useAuth();
  const [resumen, setResumen] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    api.get('/dashboard/resumen')
      .then(({ data }) => {
        if (mounted) setResumen(data);
      })
      .catch((err) => {
        toast.error('No se pudo cargar el resumen', {
          description: err.response?.data?.message ?? err.message,
        });
      })
      .finally(() => mounted && setLoading(false));
    return () => { mounted = false; };
  }, []);

  return (
    <div className="space-y-6 max-w-7xl">
      <div>
        <h1 className="text-2xl font-bold text-text-strong">Bienvenido, {user?.username}</h1>
        <p className="text-text-muted text-sm capitalize">Panel de control — {HOY_FECHA_ES}</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          titulo="Usuarios Totales"
          valor={loading ? '—' : (resumen?.totalUsuarios ?? 0)}
          descripcion="Activos en el sistema"
          accentBg="bg-badge-new-bg"
          accentText="text-primary"
          IconCmp={Users}
        />
        <StatCard
          titulo="Contactos"
          valor={loading ? '—' : (resumen?.totalContactos ?? 0)}
          descripcion={`${resumen?.contactosNuevos ?? 0} nuevos sin leer`}
          accentBg="bg-emerald-50"
          accentText="text-success"
          IconCmp={Mail}
        />
        <StatCard
          titulo="Perfiles"
          valor={loading ? '—' : (resumen?.totalPerfiles ?? 0)}
          descripcion="Configurados"
          accentBg="bg-indigo-50"
          accentText="text-info"
          IconCmp={Shield}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <ActionCard
          titulo="Usuarios"
          descripcion="Alta, baja y modificación de usuarios del sistema."
          ruta="/usuarios"
          IconCmp={Users}
          color={{ bg: 'bg-badge-new-bg', text: 'text-primary', btnBg: 'bg-primary', btnHover: 'hover:bg-primary-hover' }}
        />
        <ActionCard
          titulo="Perfiles"
          descripcion="Administrar perfiles y permisos por menú."
          ruta="/perfiles"
          IconCmp={Shield}
          color={{ bg: 'bg-indigo-50', text: 'text-info', btnBg: 'bg-info', btnHover: 'hover:opacity-90' }}
        />
        <ActionCard
          titulo="Contactos"
          descripcion="Bandeja de consultas y mensajes recibidos."
          ruta="/contactos"
          IconCmp={Mail}
          color={{ bg: 'bg-emerald-50', text: 'text-success', btnBg: 'bg-success', btnHover: 'hover:opacity-90' }}
        />
      </div>

      <div className="bg-white rounded-lg shadow-card">
        <div className="flex items-center justify-between px-6 py-4 border-b border-border-soft">
          <h2 className="font-semibold text-text-strong">Últimos Contactos</h2>
          <Link to="/contactos" className="text-sm text-primary hover:text-primary-hover font-medium">
            Ver todos →
          </Link>
        </div>
        <table className="w-full text-sm">
          <thead className="text-left text-text-muted text-xs uppercase tracking-wider">
            <tr>
              <th className="px-6 py-3 font-medium">Contacto</th>
              <th className="px-6 py-3 font-medium">Estado</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-border-soft">
            {(resumen?.ultimosContactos ?? []).map((c) => (
              <tr key={c.id}>
                <td className="px-6 py-3">
                  <div className="flex items-center gap-3">
                    <div className="h-8 w-8 rounded-full bg-primary text-white flex items-center justify-center text-xs font-semibold">
                      {c.nombre.charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <div className="font-medium text-text-strong">{c.nombre}</div>
                      <div className="text-text-muted text-xs">{c.email}</div>
                    </div>
                  </div>
                </td>
                <td className="px-6 py-3">
                  <EstadoBadge estado={c.estado} />
                </td>
              </tr>
            ))}
            {(resumen?.ultimosContactos ?? []).length === 0 && !loading && (
              <tr>
                <td colSpan={2} className="px-6 py-6 text-center text-text-muted">
                  Aún no hay contactos.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
