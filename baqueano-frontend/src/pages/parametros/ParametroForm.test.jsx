import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import ParametroForm from './ParametroForm.jsx';

describe('ParametroForm', () => {
  it('en modo create acepta valores y dispara onSubmit', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(<ParametroForm mode="create" onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByLabelText(/clave/i), 'app.test');
    await user.type(screen.getByLabelText(/^valor$/i), '42');
    await user.selectOptions(screen.getByLabelText(/tipo de dato/i), 'NUMBER');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    await waitFor(() => expect(onSubmit).toHaveBeenCalledTimes(1));
    expect(onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({
        clave: 'app.test',
        valor: '42',
        tipoDato: 'NUMBER',
        editable: true,
      }),
      expect.anything(),
    );
  });

  it('en modo edit deshabilita clave y tipoDato', () => {
    render(
      <ParametroForm
        mode="edit"
        defaultValues={{ clave: 'app.nombre', valor: 'Baqueano', tipoDato: 'STRING', editable: true }}
        onSubmit={vi.fn()}
        onCancel={vi.fn()}
      />,
    );

    expect(screen.getByLabelText(/clave/i)).toBeDisabled();
    expect(screen.getByLabelText(/tipo de dato/i)).toBeDisabled();
    expect(screen.getByLabelText(/^valor$/i)).not.toBeDisabled();
  });

  it('rechaza submit con valor vacio', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn();

    render(<ParametroForm mode="create" onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByLabelText(/clave/i), 'app.test');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    expect(await screen.findByText(/requerido/i)).toBeInTheDocument();
    expect(onSubmit).not.toHaveBeenCalled();
  });
});
