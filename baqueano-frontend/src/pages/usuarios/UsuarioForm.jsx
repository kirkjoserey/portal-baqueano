import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FormField, Input, Select, Checkbox } from '../../components/ui/FormField.jsx';
import Button from '../../components/ui/Button.jsx';

const baseFields = {
  email: z.string().min(1, 'Requerido').email('Email invalido').max(150),
  nombre: z.string().min(1, 'Requerido').max(100),
  apellido: z.string().min(1, 'Requerido').max(100),
  perfilId: z.coerce.number().int().positive('Seleccione un perfil'),
  activo: z.boolean(),
};

const createSchema = z.object({
  ...baseFields,
  username: z.string().min(1, 'Requerido').max(50),
  password: z.string().min(8, 'Minimo 8 caracteres').max(100),
});

const editSchema = z.object(baseFields);

export default function UsuarioForm({ defaultValues, perfiles, onSubmit, onCancel, submitting, mode = 'create' }) {
  const schema = mode === 'create' ? createSchema : editSchema;

  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      username: '',
      email: '',
      password: '',
      nombre: '',
      apellido: '',
      perfilId: perfiles[0]?.id ?? 0,
      activo: true,
      ...defaultValues,
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {mode === 'create' && (
        <FormField label="Username" htmlFor="username" error={errors.username?.message}>
          <Input id="username" {...register('username')} />
        </FormField>
      )}

      <FormField label="Email" htmlFor="email" error={errors.email?.message}>
        <Input id="email" type="email" {...register('email')} />
      </FormField>

      {mode === 'create' && (
        <FormField label="Contrasenia" htmlFor="password" error={errors.password?.message}>
          <Input id="password" type="password" autoComplete="new-password" {...register('password')} />
        </FormField>
      )}

      <div className="grid grid-cols-2 gap-4">
        <FormField label="Nombre" htmlFor="nombre" error={errors.nombre?.message}>
          <Input id="nombre" {...register('nombre')} />
        </FormField>
        <FormField label="Apellido" htmlFor="apellido" error={errors.apellido?.message}>
          <Input id="apellido" {...register('apellido')} />
        </FormField>
      </div>

      <FormField label="Perfil" htmlFor="perfilId" error={errors.perfilId?.message}>
        <Select id="perfilId" {...register('perfilId')}>
          {perfiles.map((p) => (
            <option key={p.id} value={p.id}>{p.nombre}</option>
          ))}
        </Select>
      </FormField>

      <Checkbox id="activo" label="Activo" {...register('activo')} />

      <div className="flex justify-end gap-2 pt-2">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={submitting}>Cancelar</Button>
        <Button type="submit" disabled={submitting}>
          {submitting ? 'Guardando...' : 'Guardar'}
        </Button>
      </div>
    </form>
  );
}
