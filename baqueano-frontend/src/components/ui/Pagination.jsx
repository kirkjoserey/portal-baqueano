import Button from './Button.jsx';

// Spring Page es 0-indexed
export default function Pagination({ page, totalPages, totalElements, onChange }) {
  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-2 px-4 md:px-6 py-3 bg-white border-t border-border-soft text-sm">
      <div className="text-text-muted text-xs text-center sm:text-left">
        Pagina {(totalPages > 0 ? page + 1 : 0)} de {totalPages || 0}
        <span className="ml-2 text-text-muted/70">({totalElements} en total)</span>
      </div>
      <div className="flex gap-2">
        <Button
          variant="secondary"
          size="sm"
          disabled={page <= 0}
          onClick={() => onChange(page - 1)}
        >
          Anterior
        </Button>
        <Button
          variant="secondary"
          size="sm"
          disabled={page >= totalPages - 1}
          onClick={() => onChange(page + 1)}
        >
          Siguiente
        </Button>
      </div>
    </div>
  );
}
