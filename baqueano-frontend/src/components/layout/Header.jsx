import { useLocation } from 'react-router-dom';

const TITULOS = {
  '/dashboard': 'Dashboard',
  '/usuarios': 'Usuarios',
  '/perfiles': 'Perfiles',
  '/parametros': 'Parámetros',
  '/contactos': 'Contactos',
};

export default function Header() {
  const { pathname } = useLocation();
  const titulo = TITULOS[pathname] ?? '';

  return (
    <header className="border-b border-border-soft bg-white px-8 py-4">
      <div className="text-text-muted text-xs">Baqueano › {titulo}</div>
    </header>
  );
}
