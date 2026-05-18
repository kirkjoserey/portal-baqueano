import { useLocation } from 'react-router-dom';

const TITULOS = {
  '/usuarios': 'Usuarios',
  '/perfiles': 'Perfiles',
  '/parametros': 'Parámetros',
  '/contactos': 'Contactos',
};

export default function Placeholder() {
  const { pathname } = useLocation();
  const titulo = TITULOS[pathname] ?? 'Próximamente';

  return (
    <div className="bg-white rounded-lg shadow-card p-10 text-center">
      <h2 className="text-xl font-bold text-text-strong">{titulo}</h2>
      <p className="text-text-muted mt-2">El ABM se construye en la Fase 8.</p>
    </div>
  );
}
