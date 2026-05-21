import { useState } from 'react';
import { toast } from 'sonner';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import api from '../../api/axios.js';
import { useAuth } from '../../auth/useAuth.js';
import usePagedQuery from '../../hooks/usePagedQuery.js';
import { errorToMessage } from '../../utils/error.js';
import Table from '../../components/ui/Table.jsx';
import Pagination from '../../components/ui/Pagination.jsx';
import Modal from '../../components/ui/Modal.jsx';
import ConfirmDialog from '../../components/ui/ConfirmDialog.jsx';
import Button from '../../components/ui/Button.jsx';
import Badge from '../../components/ui/Badge.jsx';
import ProspectoForm from './ProspectoForm.jsx';

// ── helpers de presentación ──────────────────────────────────────────────────

const ESTADO_BADGE = {
  NUEVO:      'nuevo',
  CONTACTADO: 'info',
  CALIFICADO: 'success',
  CONVERTIDO: 'success',
  PERDIDO:    'muted',
};

const badgeFor = (estado) => ESTADO_BADGE[estado] ?? 'default';

// ── componente principal ─────────────────────────────────────────────────────

export default function ProspectosPage() {
  const { getPermisos } = useAuth();
  const permisos = getPermisos('/prospectos');

  const [page,        setPage]        = useState(0);
  const [modalOpen,   setModalOpen]   = useState(false);
  const [editTarget,  setEditTarget]  = useState(null);
  const [deleteTarget,setDeleteTarget]= useState(null);
  const [submitting,  setSubmitting]  = useState(false);

  const { content, totalElements, totalPages, loading, reload } =
      usePagedQuery('/prospectos', { page, size: 10, sort: 'fechaCreacion,desc' });

  // ── acciones ────────────────────────────────────────────────────────────────

  const openCreate = () => { setEditTarget(null); setModalOpen(true); };

  const openEdit = async (item) => {
    try {
      const { data } = await api.get(`/prospectos/${item.id}`);
      setEditTarget(data);
      setModalOpen(true);
    } catch (err) {
      toast.error(errorToMessage(err));
    }
  };

  const handleSubmit = async (data) => {
    setSubmitting(true);
    try {
      if (editTarget) {
        await api.put(`/prospectos/${editTarget.id}`, data);
        toast.success('Prospecto actualizado');
      } else {
        await api.post('/prospectos', data);
        toast.success('Prospecto creado');
      }
      setModalOpen(false);
      reload();
    } catch (err) {
      toast.error(errorToMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/prospectos/${deleteTarget.id}`);
      toast.success('Prospecto dado de baja');
      setDeleteTarget(null);
      reload();
    } catch (err) {
      toast.error(errorToMessage(err));
    }
  };

  // ── columnas de la tabla ─────────────────────────────────────────────────────

  const columns = [
    {
      key: 'contacto', header: 'Prospecto',
      render: (p) => (
        <div>
          <div className="font-medium text-text-strong">{p.nombre} {p.apellido}</div>
          {p.empresa && <div className="text-text-muted text-xs">{p.empresa}</div>}
        </div>
      ),
    },
    {
      key: 'email', header: 'Email', hideOnMobile: true,
      render: (p) => <span className="text-text-strong">{p.email}</span>,
    },
    {
      key: 'origen', header: 'Origen', hideOnMobile: true,
      render: (p) => (
        <Badge variant="info">{p.origen.replace('_', ' ')}</Badge>
      ),
    },
    {
      key: 'estado', header: 'Estado',
      render: (p) => (
        <Badge variant={badgeFor(p.estado)}>{p.estado}</Badge>
      ),
    },
    {
      key: 'acciones', header: '', thClass: 'text-right', tdClass: 'text-right',
      render: (p) => (
        <div className="inline-flex gap-1">
          {permisos.puedeEditar && (
            <Button variant="ghost" size="icon" onClick={() => openEdit(p)} aria-label="Editar">
              <Pencil className="h-4 w-4" />
            </Button>
          )}
          {permisos.puedeEliminar && (
            <Button variant="ghost" size="icon" onClick={() => setDeleteTarget(p)} aria-label="Dar de baja">
              <Trash2 className="h-4 w-4 text-red-600" />
            </Button>
          )}
        </div>
      ),
    },
  ];

  // ── render ───────────────────────────────────────────────────────────────────

  return (
    <div className="space-y-4">
      {/* Cabecera */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-text-strong">Prospectos</h1>
        {permisos.puedeCrear && (
          <Button onClick={openCreate}>
            <Plus className="h-4 w-4" />
            Nuevo
          </Button>
        )}
      </div>

      {/* Tabla */}
      <Table
        columns={columns}
        rows={content}
        loading={loading}
        emptyMessage="Sin prospectos registrados"
      />
      <Pagination
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        onChange={setPage}
      />

      {/* Modal alta / edición */}
      <Modal
        open={modalOpen}
        title={editTarget ? 'Editar prospecto' : 'Nuevo prospecto'}
        onClose={() => setModalOpen(false)}
        size="lg"
      >
        <ProspectoForm
          defaultValues={editTarget ?? undefined}
          onSubmit={handleSubmit}
          onCancel={() => setModalOpen(false)}
          submitting={submitting}
        />
      </Modal>

      {/* Confirmación baja */}
      <ConfirmDialog
        open={!!deleteTarget}
        title="Dar de baja al prospecto"
        message={`¿Dar de baja a "${deleteTarget?.nombre} ${deleteTarget?.apellido}"? La operación es reversible (baja lógica).`}
        confirmLabel="Dar de baja"
        onConfirm={handleDelete}
        onClose={() => setDeleteTarget(null)}
      />
    </div>
  );
}
