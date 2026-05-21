export default function Table({ columns, rows, loading, emptyMessage = 'Sin resultados' }) {
  return (
    <div className="bg-white rounded-lg shadow-card overflow-hidden">
      {/* overflow-x-auto: en pantallas chicas la tabla scrollea horizontalmente */}
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-bg-app text-text-muted text-xs uppercase tracking-wider">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className={[
                    'px-4 md:px-6 py-3 text-left font-medium whitespace-nowrap',
                    col.hideOnMobile ? 'hidden sm:table-cell' : '',
                    col.thClass ?? '',
                  ].join(' ')}
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-border-soft">
            {loading && (
              <tr>
                <td colSpan={columns.length} className="px-4 md:px-6 py-6 text-center text-text-muted">
                  Cargando...
                </td>
              </tr>
            )}
            {!loading && rows.length === 0 && (
              <tr>
                <td colSpan={columns.length} className="px-4 md:px-6 py-6 text-center text-text-muted">
                  {emptyMessage}
                </td>
              </tr>
            )}
            {!loading && rows.map((row, i) => (
              <tr key={row.id ?? i} className="hover:bg-bg-app/50">
                {columns.map((col) => (
                  <td
                    key={col.key}
                    className={[
                      'px-4 md:px-6 py-3 text-text-strong',
                      col.hideOnMobile ? 'hidden sm:table-cell' : '',
                      col.tdClass ?? '',
                    ].join(' ')}
                  >
                    {col.render ? col.render(row) : row[col.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
