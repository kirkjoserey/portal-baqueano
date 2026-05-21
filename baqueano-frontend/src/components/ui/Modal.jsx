import { useEffect } from 'react';
import { X } from 'lucide-react';

export default function Modal({ open, title, onClose, children, size = 'md' }) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => e.key === 'Escape' && onClose?.();
    document.addEventListener('keydown', onKey);
    return () => document.removeEventListener('keydown', onKey);
  }, [open, onClose]);

  if (!open) return null;

  // px-4 en el contenedor externo garantiza margen a los costados en mobile.
  // max-w-* actua como limite en pantallas grandes.
  const widths = { sm: 'max-w-sm', md: 'max-w-lg', lg: 'max-w-2xl' };

  return (
    <div
      className="fixed inset-0 z-50 bg-black/40 flex items-end sm:items-center justify-center px-0 sm:px-4"
      onClick={onClose}
    >
      <div
        className={[
          'bg-white w-full shadow-card',
          /* en mobile ocupa la parte inferior (sheet), en sm+ es un modal centrado */
          'rounded-t-2xl sm:rounded-lg',
          widths[size],
          'max-h-[92vh] sm:max-h-[90vh] overflow-y-auto',
        ].join(' ')}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between px-4 md:px-6 py-4 border-b border-border-soft">
          <h2 className="font-semibold text-text-strong">{title}</h2>
          <button
            onClick={onClose}
            aria-label="Cerrar"
            className="p-1.5 rounded-md text-text-muted hover:text-text-strong hover:bg-bg-app transition-colors"
          >
            <X className="h-5 w-5" />
          </button>
        </div>
        <div className="p-4 md:p-6">{children}</div>
      </div>
    </div>
  );
}
