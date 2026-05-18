import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import UsuarioForm from './UsuarioForm.jsx';

const PERFILES = [
  { id: 1, nombre: 'ADMIN' },
  { id: 2, nombre: 'GESTOR' },
  { id: 3, nombre: 'CONSULTA' },
];

describe('UsuarioForm', () => {
  it('en modo create exige username, email, password, nombre y apellido', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(<UsuarioForm mode="create" perfiles={PERFILES} onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // Debe haber al menos un mensaje de "requerido"
    expect(await screen.findAllByText(/requerido/i)).not.toHaveLength(0);
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('valida formato de email y bloquea submit', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(<UsuarioForm mode="create" perfiles={PERFILES} onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByLabelText(/username/i), 'pepe');
    await user.type(screen.getByLabelText(/^email$/i), 'no-es-email');
    await user.type(screen.getByLabelText(/contrasenia/i), 'secret12345');
    await user.type(screen.getByLabelText(/^nombre$/i), 'Pepe');
    await user.type(screen.getByLabelText(/apellido/i), 'Tester');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // El mensaje exacto depende de la version de zod (4 cambio el wording);
    // verificamos el comportamiento: con email mal formado, no se llega a onSubmit.
    await new Promise((r) => setTimeout(r, 50));
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('en modo edit no muestra los campos username ni password', () => {
    render(
      <UsuarioForm
        mode="edit"
        perfiles={PERFILES}
        defaultValues={{
          email: 'admin@x.com',
          nombre: 'Admin',
          apellido: 'Sis',
          perfilId: 1,
          activo: true,
        }}
        onSubmit={vi.fn()}
        onCancel={vi.fn()}
      />,
    );

    expect(screen.queryByLabelText(/username/i)).not.toBeInTheDocument();
    expect(screen.queryByLabelText(/contrasenia/i)).not.toBeInTheDocument();
    expect(screen.getByLabelText(/^email$/i)).toHaveValue('admin@x.com');
  });

  it('llama onSubmit con los valores cuando el form es valido', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(<UsuarioForm mode="create" perfiles={PERFILES} onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByLabelText(/username/i), 'pepe');
    await user.type(screen.getByLabelText(/^email$/i), 'pepe@example.com');
    await user.type(screen.getByLabelText(/contrasenia/i), 'secret12345');
    await user.type(screen.getByLabelText(/^nombre$/i), 'Pepe');
    await user.type(screen.getByLabelText(/apellido/i), 'Tester');
    await user.selectOptions(screen.getByLabelText(/perfil/i), '2');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    await waitFor(() => expect(onSubmit).toHaveBeenCalledTimes(1));
    expect(onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({
        username: 'pepe',
        email: 'pepe@example.com',
        password: 'secret12345',
        nombre: 'Pepe',
        apellido: 'Tester',
        perfilId: 2,
      }),
      expect.anything(),
    );
  });
});
