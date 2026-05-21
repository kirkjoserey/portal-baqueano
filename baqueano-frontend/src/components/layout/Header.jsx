import { useLocation } from 'react-router-dom';
import { Menu } from 'lucide-react';

const TITULOS = {
  '/dashboard': 'Dashboard',
  '/usuarios': 'Usuarios',
  '/perfiles': 'Perfiles',
  '/parametros': 'Parámetros',
  '/contactos': 'Contactos',
  '/prospectos': 'Prospectos',
};

export default function Header({ onMenuClick }) {
  const { pathname } = useLocation();
  const titulo = TITULOS[pathname] ?? '';

  return (
    <header className="border-b border-border-soft bg-white px-4 md:px-8 py-4 flex items-center gap-3">
      {/* Hamburguesa — visible solo en mobile */}
      <button
        type="button"
        onClick={onMenuClick}
        className="md:hidden p-2 -ml-1 rounded-md text-text-muted hover:bg-bg-app transition-colors"
        aria-label="Abrir menú"
      >
        <Menu className="h-5 w-5" />
      </button>
      <div className="text-text-muted text-xs">Baqueano › {titulo}</div>
    </header>
  );
}
