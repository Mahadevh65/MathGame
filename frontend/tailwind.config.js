/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        realm: {
          void: '#0b0e1a',
          panel: '#12172b',
          glass: 'rgba(255,255,255,0.06)',
          primary: '#7c5cff',
          secondary: '#37e6c0',
          amber: '#ffb648',
          danger: '#ff6b6b'
        }
      },
      boxShadow: {
        glow: '0 0 40px rgba(124, 92, 255, 0.35)',
        glowGreen: '0 0 30px rgba(55, 230, 192, 0.35)'
      }
    }
  },
  plugins: []
}
