import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FormField, Input, Select, Checkbox } from '../../components/ui/FormField.jsx';
import Button from '../../components/ui/Button.jsx';

const TIPOS = ['STRING', 'NUMBER', 'BOOLEAN', 'JSON', 'DATE'];

const schema = z.object({
  clave: z.string().min(1, 'Requerido').max(100),
  valor: z.string().min(1, 'Requerido').max(500),
  descripcion: z.string().max(255).optional().or(z.literal('')),
  tipoDato: z.enum(TIPOS),
  editable: z.boolean(),
});

export default function ParametroForm({ defaultValues, onSubmit, onCancel, submitting, mode = 'create' }) {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      clave: '',
      valor: '',
      descripcion: '',
      tipoDato: 'STRING',
      editable: true,
      ...defaultValues,
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <FormField label="Clave" htmlFor="clave" error={errors.clave?.message}>
        <Input id="clave" {...register('clave')} disabled={mode === 'edit'} />
      </FormField>

      <FormField label="Valor" htmlFor="valor" error={errors.valor?.message}>
        <Input id="valor" {...register('valor')} />
      </FormField>

      <FormField label="Descripcion" htmlFor="descripcion" error={errors.descripcion?.message}>
        <Input id="descripcion" {...register('descripcion')} />
      </FormField>

      <FormField label="Tipo de dato" htmlFor="tipoDato" error={errors.tipoDato?.message}>
        <Select id="tipoDato" {...register('tipoDato')} disabled={mode === 'edit'}>
          {TIPOS.map((t) => <option key={t} value={t}>{t}</option>)}
        </Select>
      </FormField>

      <Checkbox id="editable" label="Editable" {...register('editable')} />

      <div className="flex justify-end gap-2 pt-2">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={submitting}>Cancelar</Button>
        <Button type="submit" disabled={submitting}>
          {submitting ? 'Guardando...' : 'Guardar'}
        </Button>
      </div>
    </form>
  );
}
