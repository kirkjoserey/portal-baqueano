import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FormField, Input, Checkbox } from '../../components/ui/FormField.jsx';
import Button from '../../components/ui/Button.jsx';

const schema = z.object({
  nombre: z.string().min(1, 'Requerido').max(50, 'Maximo 50'),
  descripcion: z.string().max(255, 'Maximo 255').optional().or(z.literal('')),
  activo: z.boolean(),
});

export default function PerfilForm({ defaultValues, onSubmit, onCancel, submitting }) {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      nombre: '',
      descripcion: '',
      activo: true,
      ...defaultValues,
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <FormField label="Nombre" htmlFor="nombre" error={errors.nombre?.message}>
        <Input id="nombre" {...register('nombre')} />
      </FormField>

      <FormField label="Descripcion" htmlFor="descripcion" error={errors.descripcion?.message}>
        <Input id="descripcion" {...register('descripcion')} />
      </FormField>

      <Checkbox id="activo" label="Activo" {...register('activo')} />

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
