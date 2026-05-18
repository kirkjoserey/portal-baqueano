import forms from '@tailwindcss/forms';

/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        // Paleta de la spec (seccion 6.1)
        primary: {
          DEFAULT: '#DC2626',
          hover: '#B91C1C',
        },
        'bg-app': '#F3F4F6',
        'bg-sidebar': '#1F2125',
        'bg-sidebar-deep': '#17181B',
        'text-on-dark': '#FFFFFF',
        'text-muted-dark': '#9CA3AF',
        'text-strong': '#111827',
        'text-muted': '#6B7280',
        'border-soft': '#E5E7EB',
        success: '#10B981',
        info: '#6366F1',
        'badge-new-bg': '#FEE2E2',
        'badge-new-text': '#DC2626',
        'badge-read-bg': '#F3F4F6',
        'badge-read-text': '#6B7280',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'Segoe UI', 'sans-serif'],
      },
      boxShadow: {
        soft: '0 1px 2px rgba(0,0,0,0.04)',
        card: '0 4px 12px rgba(0,0,0,0.06)',
      },
      borderRadius: {
        sm: '6px',
        md: '10px',
        lg: '14px',
      },
    },
  },
  plugins: [forms],
};
