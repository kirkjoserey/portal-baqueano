export default function Table({ columns, rows, loading, emptyMessage = 'Sin resultados' }) {
  return (
    <div className="bg-white rounded-lg shadow-card overflow-hidden">
      <table className="w-full text-sm">
        <thead className="bg-bg-app text-text-muted text-xs uppercase tracking-wider">
          <tr>
            {columns.map((col) => (
              <th key={col.key} className={`px-6 py-3 text-left font-medium ${col.thClass ?? ''}`}>
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-border-soft">
          {loading && (
            <tr>
              <td colSpan={columns.length} className="px-6 py-6 text-center text-text-muted">
                Cargando...
              </td>
            </tr>
          )}
          {!loading && rows.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-6 py-6 text-center text-text-muted">
                {emptyMessage}
              </td>
            </tr>
          )}
          {!loading && rows.map((row, i) => (
            <tr key={row.id ?? i} className="hover:bg-bg-app/50">
              {columns.map((col) => (
                <td key={col.key} className={`px-6 py-3 text-text-strong ${col.tdClass ?? ''}`}>
                  {col.render ? col.render(row) : row[col.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
