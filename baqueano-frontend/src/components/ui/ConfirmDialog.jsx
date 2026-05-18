import Modal from './Modal.jsx';
import Button from './Button.jsx';

export default function ConfirmDialog({
  open, title = 'Confirmar', message, confirmLabel = 'Eliminar',
  onConfirm, onClose, danger = true,
}) {
  return (
    <Modal open={open} title={title} onClose={onClose} size="sm">
      <p className="text-text-strong">{message}</p>
      <div className="mt-6 flex justify-end gap-2">
        <Button variant="secondary" onClick={onClose}>Cancelar</Button>
        <Button variant={danger ? 'danger' : 'primary'} onClick={onConfirm}>{confirmLabel}</Button>
      </div>
    </Modal>
  );
}
