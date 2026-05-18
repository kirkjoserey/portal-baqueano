import {
  Home,
  Settings,
  Users,
  Shield,
  Mail,
  Zap,
  LogOut,
  Plus,
  Pencil,
  Trash2,
  Search,
  Eye,
} from 'lucide-react';

// Mapa de nombres de icono (lo que el backend guarda en submenu.icono) -> componente.
// Si llega un nombre desconocido, devolvemos Home como fallback visible.
const REGISTRO = {
  home: Home,
  settings: Settings,
  users: Users,
  shield: Shield,
  mail: Mail,
  zap: Zap,
  'log-out': LogOut,
  plus: Plus,
  pencil: Pencil,
  trash: Trash2,
  search: Search,
  eye: Eye,
};

export default function Icon({ name, className = 'h-5 w-5', strokeWidth = 2 }) {
  const Component = REGISTRO[name] ?? Home;
  return <Component className={className} strokeWidth={strokeWidth} />;
}
