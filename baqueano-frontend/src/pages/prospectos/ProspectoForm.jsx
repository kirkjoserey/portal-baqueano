import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FormField, Input, Select, Checkbox, Textarea } from '../../components/ui/FormField.jsx';
import Button from '../../components/ui/Button.jsx';

const ESTADOS  = ['NUEVO', 'CONTACTADO', 'CALIFICADO', 'PERDIDO', 'CONVERTIDO'];
const ORIGENES = ['REFERIDO', 'WEB', 'RED_SOCIAL', 'LLAMADA', 'EMAIL', 'OTRO'];

const schema = z.object({
  nombre:   z.string().min(1, 'Requerido').max(100),
  apellido: z.string().min(1, 'Requerido').max(100),
  empresa:  z.string().max(150).optional().or(z.literal('')),
  email:    z.string().min(1, 'Requerido').email('Email inválido').max(150),
  telefono: z.string().max(30).optional().or(z.literal('')),
  estado:   z.enum(ESTADOS),
  origen:   z.enum(ORIGENES),
  notas:    z.string().optional().or(z.literal('')),
  activo:   z.boolean(),
});

export default function ProspectoForm({ defaultValues, onSubmit, onCancel, submitting }) {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      nombre:   '',
      apellido: '',
      empresa:  '',
      email:    '',
      telefono: '',
      estado:   'NUEVO',
      origen:   'WEB',
      notas:    '',
      activo:   true,
      ...defaultValues,
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">

      {/* Nombre + Apellido */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <FormField label="Nombre" htmlFor="nombre" error={errors.nombre?.message}>
          <Input id="nombre" {...register('nombre')} />
        </FormField>
        <FormField label="Apellido" htmlFor="apellido" error={errors.apellido?.message}>
          <Input id="apellido" {...register('apellido')} />
        </FormField>
      </div>

      {/* Empresa + Email */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <FormField label="Empresa" htmlFor="empresa" error={errors.empresa?.message}>
          <Input id="empresa" {...register('empresa')} placeholder="Opcional" />
        </FormField>
        <FormField label="Email" htmlFor="email" error={errors.email?.message}>
          <Input id="email" type="email" {...register('email')} />
        </FormField>
      </div>

      {/* Teléfono + Origen */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <FormField label="Teléfono" htmlFor="telefono" error={errors.telefono?.message}>
          <Input id="telefono" {...register('telefono')} placeholder="Opcional" />
        </FormField>
        <FormField label="Origen" htmlFor="origen" error={errors.origen?.message}>
          <Select id="origen" {...register('origen')}>
            {ORIGENES.map((o) => (
              <option key={o} value={o}>{o.replace('_', ' ')}</option>
            ))}
          </Select>
        </FormField>
      </div>

      {/* Estado */}
      <FormField label="Estado" htmlFor="estado" error={errors.estado?.message}>
        <Select id="estado" {...register('estado')}>
          {ESTADOS.map((e) => (
            <option key={e} value={e}>{e}</option>
          ))}
        </Select>
      </FormField>

      {/* Notas */}
      <FormField label="Notas" htmlFor="notas" error={errors.notas?.message}>
        <Textarea id="notas" rows={3} {...register('notas')} placeholder="Observaciones internas..." />
      </FormField>

      {/* Activo */}
      <Checkbox id="activo" label="Activo" {...register('activo')} />

      {/* Botones */}
      <div className="flex justify-end gap-2 pt-2">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={submitting}>
          Cancelar
        </Button>
        <Button type="submit" disabled={submitting}>
          {submitting ? 'Guardando...' : 'Guardar'}
        </Button>
      </div>
    </form>
  );
}
