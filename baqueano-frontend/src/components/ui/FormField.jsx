import { forwardRef } from 'react';

export function FormField({ label, htmlFor, error, children }) {
  return (
    <div>
      <label htmlFor={htmlFor} className="block text-sm font-medium text-text-strong mb-1">
        {label}
      </label>
      {children}
      {error && <p className="mt-1 text-xs text-primary">{error}</p>}
    </div>
  );
}

const INPUT_BASE =
  'w-full rounded-md border-border-soft shadow-sm focus:border-primary focus:ring-primary disabled:bg-bg-app disabled:cursor-not-allowed';

// forwardRef indispensable: react-hook-form usa el ref para enganchar el
// input real y aplicar defaultValues + leer valores. Sin esto el ref se pierde.

export const Input = forwardRef(function Input({ className = '', ...rest }, ref) {
  return <input ref={ref} className={`${INPUT_BASE} ${className}`} {...rest} />;
});

export const Textarea = forwardRef(function Textarea({ className = '', rows = 4, ...rest }, ref) {
  return <textarea ref={ref} rows={rows} className={`${INPUT_BASE} ${className}`} {...rest} />;
});

export const Select = forwardRef(function Select({ className = '', children, ...rest }, ref) {
  return (
    <select ref={ref} className={`${INPUT_BASE} ${className}`} {...rest}>
      {children}
    </select>
  );
});

export const Checkbox = forwardRef(function Checkbox({ id, label, ...rest }, ref) {
  return (
    <label htmlFor={id} className="inline-flex items-center gap-2 text-sm text-text-strong">
      <input ref={ref} id={id} type="checkbox" className="rounded text-primary focus:ring-primary" {...rest} />
      {label}
    </label>
  );
});
