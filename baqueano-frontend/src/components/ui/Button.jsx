const VARIANTS = {
  primary: 'bg-primary text-white hover:bg-primary-hover',
  secondary: 'bg-white text-text-strong border border-border-soft hover:bg-bg-app',
  danger: 'bg-red-600 text-white hover:bg-red-700',
  ghost: 'text-text-muted hover:bg-bg-app',
  info: 'bg-info text-white hover:opacity-90',
  success: 'bg-success text-white hover:opacity-90',
};

const SIZES = {
  sm:   'px-3 py-1.5 text-xs min-h-[36px]',
  md:   'px-4 py-2 text-sm min-h-[40px]',
  /* icon: minimo 44x44 px para que el touch target sea accesible en mobile */
  icon: 'p-2 min-h-[44px] min-w-[44px] justify-center',
};

export default function Button({ variant = 'primary', size = 'md', className = '', children, ...rest }) {
  return (
    <button
      className={`${VARIANTS[variant]} ${SIZES[size]} font-medium rounded-md transition-colors disabled:opacity-60 disabled:cursor-not-allowed inline-flex items-center gap-2 ${className}`}
      {...rest}
    >
      {children}
    </button>
  );
}
