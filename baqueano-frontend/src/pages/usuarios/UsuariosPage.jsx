import { useState } from 'react';
import { toast } from 'sonner';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import api from '../../api/axios.js';
import { useAuth } from '../../auth/useAuth.js';
import usePagedQuery from '../../hooks/usePagedQuery.js';
import usePerfilesAll from '../../hooks/usePerfilesAll.js';
import { errorToMessage } from '../../utils/error.js';
import Table from '../../components/ui/Table.jsx';
import Pagination from '../../components/ui/Pagination.jsx';
import Modal from '../../components/ui/Modal.jsx';
import ConfirmDialog from '../../components/ui/ConfirmDialog.jsx';
import Button from '../../components/ui/Button.jsx';
import Badge from '../../components/ui/Badge.jsx';
import UsuarioForm from './UsuarioForm.jsx';

export default function UsuariosPage() {
  const { getPermisos } = useAuth();
  const permisos = getPermisos('/usuarios');
  const { perfiles } = usePerfilesAll();

  const [page, setPage] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);  // UsuarioResponseDTO (con perfilId)
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const { content, totalElements, totalPages, loading, reload } =
      usePagedQuery('/usuarios', { page, size: 10, sort: 'username,asc' });

  const openCreate = () => { setEditTarget(null); setModalOpen(true); };

  const openEdit = async (item) => {
    // El list item no trae perfilId; pedimos el ResponseDTO completo
    try {
      const { data } = await api.get(`/usuarios/${item.id}`);
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
        // En edit no enviamos password ni username
        const { password, username, ...payload } = data;
        await api.put(`/usuarios/${editTarget.id}`, payload);
        toast.success('Usuario actualizado');
      } else {
        await api.post('/usuarios', data);
        toast.success('Usuario creado');
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
      await api.delete(`/usuarios/${deleteTarget.id}`);
      toast.success('Usuario dado de baja');
      setDeleteTarget(null);
      reload();
    } catch (err) {
      toast.error(errorToMessage(err));
    }
  };

  const columns = [
    { key: 'username', header: 'Usuario' },
    { key: 'email', header: 'Email', hideOnMobile: true },
    { key: 'nombreCompleto', header: 'Nombre', render: (u) => `${u.nombre} ${u.apellido}` },
    { key: 'perfilNombre', header: 'Perfil', hideOnMobile: true },
    {
      key: 'activo', header: 'Estado',
      render: (u) => <Badge variant={u.activo ? 'success' : 'muted'}>{u.activo ? 'Activo' : 'Inactivo'}</Badge>,
    },
    {
      key: 'acciones', header: '', thClass: 'text-right', tdClass: 'text-right',
      render: (u) => (
        <div className="inline-flex gap-1">
          {permisos.puedeEditar && (
            <Button variant="ghost" size="icon" onClick={() => openEdit(u)} aria-label="Editar">
              <Pencil className="h-4 w-4" />
            </Button>
          )}
          {permisos.puedeEliminar && (
            <Button variant="ghost" size="icon" onClick={() => setDeleteTarget(u)} aria-label="Eliminar">
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
        <h1 className="text-2xl font-bold text-text-strong">Usuarios</h1>
        {permisos.puedeCrear && (
          <Button onClick={openCreate}><Plus className="h-4 w-4" />Nuevo</Button>
        )}
      </div>

      <Table columns={columns} rows={content} loading={loading} emptyMessage="Sin usuarios cargados" />
      <Pagination page={page} totalPages={totalPages} totalElements={totalElements} onChange={setPage} />

      <Modal
        open={modalOpen}
        title={editTarget ? 'Editar usuario' : 'Nuevo usuario'}
        onClose={() => setModalOpen(false)}
      >
        <UsuarioForm
          mode={editTarget ? 'edit' : 'create'}
          defaultValues={editTarget}
          perfiles={perfiles}
          onSubmit={handleSubmit}
          onCancel={() => setModalOpen(false)}
          submitting={submitting}
        />
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Dar de baja al usuario"
        message={`Dar de baja a "${deleteTarget?.username}"? La operacion es reversible (baja logica).`}
        confirmLabel="Dar de baja"
        onConfirm={handleDelete}
        onClose={() => setDeleteTarget(null)}
      />
    </div>
  );
}
