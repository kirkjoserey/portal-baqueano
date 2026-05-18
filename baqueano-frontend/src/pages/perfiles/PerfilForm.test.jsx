import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import PerfilForm from './PerfilForm.jsx';

describe('PerfilForm', () => {
  it('muestra error de validacion si nombre vacio y no llama onSubmit', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(<PerfilForm onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.click(screen.getByRole('button', { name: /guardar/i }));

    expect(await screen.findByText(/requerido/i)).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('llama onSubmit con los valores cuando el form es valido', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(<PerfilForm onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByLabelText(/nombre/i), 'NUEVO');
    await user.type(screen.getByLabelText(/descripcion/i), 'descripcion de prueba');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    await waitFor(() => expect(onSubmit).toHaveBeenCalledTimes(1));
    expect(onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({
        nombre: 'NUEVO',
        descripcion: 'descripcion de prueba',
        activo: true,
      }),
      expect.anything(),
    );
  });

  it('al pasar defaultValues precarga el form', () => {
    render(
      <PerfilForm
        defaultValues={{ nombre: 'ADMIN', descripcion: 'Acceso total', activo: false }}
        onSubmit={vi.fn()}
        onCancel={vi.fn()}
      />,
    );

    expect(screen.getByLabelText(/nombre/i)).toHaveValue('ADMIN');
    expect(screen.getByLabelText(/descripcion/i)).toHaveValue('Acceso total');
    expect(screen.getByLabelText(/activo/i)).not.toBeChecked();
  });

  it('clic en Cancelar dispara onCancel', async () => {
    const user = userEvent.setup();
    const onCancel = vi.fn();

    render(<PerfilForm onSubmit={vi.fn()} onCancel={onCancel} />);

    await user.click(screen.getByRole('button', { name: /cancelar/i }));
    expect(onCancel).toHaveBeenCalledTimes(1);
  });
});
