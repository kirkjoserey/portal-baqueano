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
import PerfilForm from './PerfilForm.jsx';

export default function PerfilesPage() {
  const { getPermisos } = useAuth();
  const permisos = getPermisos('/perfiles');

  const [page, setPage] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const { content, totalElements, totalPages, loading, reload } =
      usePagedQuery('/perfiles', { page, size: 10, sort: 'nombre,asc' });

  const openCreate = () => { setEditTarget(null); setModalOpen(true); };
  const openEdit = (p) => { setEditTarget(p); setModalOpen(true); };

  const handleSubmit = async (data) => {
    setSubmitting(true);
    try {
      if (editTarget) {
        await api.put(`/perfiles/${editTarget.id}`, data);
        toast.success('Perfil actualizado');
      } else {
        await api.post('/perfiles', data);
        toast.success('Perfil creado');
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
      await api.delete(`/perfiles/${deleteTarget.id}`);
      toast.success('Perfil eliminado');
      setDeleteTarget(null);
      reload();
    } catch (err) {
      toast.error(errorToMessage(err));
    }
  };

  const columns = [
    { key: 'nombre', header: 'Nombre' },
    { key: 'descripcion', header: 'Descripcion' },
    {
      key: 'activo', header: 'Estado',
      render: (p) => <Badge variant={p.activo ? 'success' : 'muted'}>{p.activo ? 'Activo' : 'Inactivo'}</Badge>,
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
            <Button variant="ghost" size="icon" onClick={() => setDeleteTarget(p)} aria-label="Eliminar">
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
        <h1 className="text-2xl font-bold text-text-strong">Perfiles</h1>
        {permisos.puedeCrear && (
          <Button onClick={openCreate}><Plus className="h-4 w-4" />Nuevo</Button>
        )}
      </div>

      <Table columns={columns} rows={content} loading={loading} emptyMessage="Sin perfiles cargados" />
      <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onChange={setPage} />

      <Modal open={modalOpen} title={editTarget ? 'Editar perfil' : 'Nuevo perfil'} onClose={() => setModalOpen(false)}>
        <PerfilForm
          defaultValues={editTarget}
          onSubmit={handleSubmit}
          onCancel={() => setModalOpen(false)}
          submitting={submitting}
        />
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Eliminar perfil"
        message={`Eliminar el perfil "${deleteTarget?.nombre}"? Esta accion no se puede deshacer.`}
        onConfirm={handleDelete}
        onClose={() => setDeleteTarget(null)}
      />
    </div>
  );
}
