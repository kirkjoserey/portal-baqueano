import { useState } from 'react';
import { toast } from 'sonner';
import { Eye, Trash2 } from 'lucide-react';
import api from '../../api/axios.js';
import { useAuth } from '../../auth/useAuth.js';
import usePagedQuery from '../../hooks/usePagedQuery.js';
import { errorToMessage } from '../../utils/error.js';
import Table from '../../components/ui/Table.jsx';
import Pagination from '../../components/ui/Pagination.jsx';
import ConfirmDialog from '../../components/ui/ConfirmDialog.jsx';
import Button from '../../components/ui/Button.jsx';
import Badge from '../../components/ui/Badge.jsx';
import { Select } from '../../components/ui/FormField.jsx';
import ContactoDetalleModal from './ContactoDetalleModal.jsx';

const ESTADOS = ['', 'NUEVO', 'LEIDO', 'RESPONDIDO', 'ARCHIVADO'];
const badgeFor = (e) => ({ NUEVO: 'nuevo', LEIDO: 'default', RESPONDIDO: 'success', ARCHIVADO: 'muted' }[e] ?? 'default');

export default function ContactosPage() {
  const { getPermisos } = useAuth();
  const permisos = getPermisos('/contactos');

  const [page, setPage] = useState(0);
  const [estadoFiltro, setEstadoFiltro] = useState('');
  const [detalle, setDetalle] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const filters = estadoFiltro ? { estado: estadoFiltro } : undefined;

  const { content, totalElements, totalPages, loading, reload } =
      usePagedQuery('/contactos', { page, size: 10, sort: 'fechaCreacion,desc', filters });

  const abrirDetalle = async (item) => {
    try {
      const { data } = await api.get(`/contactos/${item.id}`);
      setDetalle(data);
    } catch (err) {
      toast.error(errorToMessage(err));
    }
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/contactos/${deleteTarget.id}`);
      toast.success('Contacto eliminado');
      setDeleteTarget(null);
      reload();
    } catch (err) {
      toast.error(errorToMessage(err));
    }
  };

  const columns = [
    { key: 'nombre', header: 'Contacto', render: (c) => (
      <div>
        <div className="font-medium text-text-strong">{c.nombre}</div>
        <div className="text-text-muted text-xs">{c.email}</div>
      </div>
    )},
    { key: 'asunto', header: 'Asunto', render: (c) => c.asunto ?? <span className="text-text-muted/60">—</span> },
    {
      key: 'estado', header: 'Estado',
      render: (c) => <Badge variant={badgeFor(c.estado)}>{c.estado}</Badge>,
    },
    {
      key: 'fechaCreacion', header: 'Recibido',
      render: (c) => new Date(c.fechaCreacion).toLocaleDateString('es-AR'),
    },
    {
      key: 'acciones', header: '', thClass: 'text-right', tdClass: 'text-right',
      render: (c) => (
        <div className="inline-flex gap-1">
          <Button variant="ghost" size="icon" onClick={() => abrirDetalle(c)} aria-label="Ver detalle">
            <Eye className="h-4 w-4" />
          </Button>
          {permisos.puedeEliminar && (
            <Button variant="ghost" size="icon" onClick={() => setDeleteTarget(c)} aria-label="Eliminar">
              <Trash2 className="h-4 w-4 text-red-600" />
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-text-strong">Contactos</h1>
        <div className="flex items-center gap-2">
          <label htmlFor="estado-filtro" className="text-text-muted text-sm">Estado:</label>
          <Select
            id="estado-filtro"
            value={estadoFiltro}
            onChange={(e) => { setEstadoFiltro(e.target.value); setPage(0); }}
            className="!w-44"
          >
            {ESTADOS.map((e) => (
              <option key={e || 'all'} value={e}>{e || 'Todos'}</option>
            ))}
          </Select>
        </div>
      </div>

      <Table columns={columns} rows={content} loading={loading} emptyMessage="Sin contactos" />
      <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onChange={setPage} />

      <ContactoDetalleModal
        open={!!detalle}
        contacto={detalle}
        onClose={() => setDetalle(null)}
        onChanged={reload}
        puedeEditar={permisos.puedeEditar}
      />

      <ConfirmDialog
        open={!!deleteTarget}
        title="Eliminar contacto"
        message={`Eliminar el contacto de ${deleteTarget?.nombre}? Esta accion no se puede deshacer.`}
        onConfirm={handleDelete}
        onClose={() => setDeleteTarget(null)}
      />
    </div>
  );
}
