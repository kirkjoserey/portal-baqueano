const VARIANTS = {
  default: 'bg-badge-read-bg text-badge-read-text',
  nuevo: 'bg-badge-new-bg text-badge-new-text',
  success: 'bg-emerald-50 text-success',
  info: 'bg-indigo-50 text-info',
  muted: 'bg-slate-100 text-slate-600',
};

export default function Badge({ variant = 'default', children }) {
  return (
    <span className={`${VARIANTS[variant]} px-2 py-1 rounded-md text-xs font-semibold inline-block`}>
      {children}
    </span>
  );
}
