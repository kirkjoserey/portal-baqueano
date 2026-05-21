import { useState } from 'react';
import { toast } from 'sonner';
import api from '../../api/axios.js';
import { errorToMessage } from '../../utils/error.js';
import Modal from '../../components/ui/Modal.jsx';
import Button from '../../components/ui/Button.jsx';
import Badge from '../../components/ui/Badge.jsx';

const ESTADOS = ['NUEVO', 'LEIDO', 'RESPONDIDO', 'ARCHIVADO'];
const badgeFor = (e) => ({ NUEVO: 'nuevo', LEIDO: 'default', RESPONDIDO: 'success', ARCHIVADO: 'muted' }[e] ?? 'default');

export default function ContactoDetalleModal({ open, contacto, onClose, onChanged, puedeEditar }) {
  const [estado, setEstado] = useState(contacto?.estado);
  const [saving, setSaving] = useState(false);

  if (!contacto) return null;

  const cambiarEstado = async (nuevoEstado) => {
    setSaving(true);
    try {
      await api.patch(`/contactos/${contacto.id}/estado`, { estado: nuevoEstado });
      setEstado(nuevoEstado);
      toast.success(`Estado cambiado a ${nuevoEstado}`);
      onChanged?.();
    } catch (err) {
      toast.error(errorToMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal open={open} title="Detalle de contacto" onClose={onClose} size="lg">
      <div className="space-y-4 text-sm">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <div className="text-text-muted text-xs">Nombre</div>
            <div className="font-medium text-text-strong">{contacto.nombre}</div>
          </div>
          <div>
            <div className="text-text-muted text-xs">Email</div>
            <div className="font-medium text-text-strong">{contacto.email}</div>
          </div>
          {contacto.telefono && (
            <div>
              <div className="text-text-muted text-xs">Telefono</div>
              <div className="text-text-strong">{contacto.telefono}</div>
            </div>
          )}
          {contacto.ipOrigen && (
            <div>
              <div className="text-text-muted text-xs">IP origen</div>
              <div className="text-text-strong">{contacto.ipOrigen}</div>
            </div>
          )}
        </div>

        {contacto.asunto && (
          <div>
            <div className="text-text-muted text-xs">Asunto</div>
            <div className="font-medium text-text-strong">{contacto.asunto}</div>
          </div>
        )}

        <div>
          <div className="text-text-muted text-xs mb-1">Mensaje</div>
          <div className="bg-bg-app rounded-md p-4 text-text-strong whitespace-pre-wrap break-words">{contacto.mensaje}</div>
        </div>

        <div className="flex items-center gap-3">
          <div className="text-text-muted text-xs">Estado actual:</div>
          <Badge variant={badgeFor(estado)}>{estado}</Badge>
        </div>

        {puedeEditar && (
          <div className="pt-2 border-t border-border-soft flex flex-wrap gap-2">
            <div className="text-text-muted text-xs w-full">Cambiar a:</div>
            {ESTADOS.filter((e) => e !== estado).map((e) => (
              <Button key={e} variant="secondary" size="sm" disabled={saving} onClick={() => cambiarEstado(e)}>
                {e}
              </Button>
            ))}
          </div>
        )}
      </div>
    </Modal>
  );
}
