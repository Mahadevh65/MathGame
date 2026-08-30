import { motion } from 'framer-motion'
import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useLogin } from '../../api/hooks'
import { useAuth } from '../../shared/AuthContext'
import { panelVariants } from '../../design-system/motion/motion'

export function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const login = useLogin()
  const { login: setAuth } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault()
    login.mutate(
      { email, password },
      {
        onSuccess: (auth) => {
          setAuth(auth)
          navigate('/dashboard')
        }
      }
    )
  }

  return (
    <div className="min-h-screen bg-realm-void flex items-center justify-center p-6">
      <motion.form
        variants={panelVariants}
        initial="initial"
        animate="animate"
        onSubmit={handleSubmit}
        className="w-full max-w-sm rounded-2xl bg-realm-panel/80 border border-white/10 p-8"
      >
        <h1 className="text-2xl font-bold text-white mb-1">The Thinking Realms</h1>
        <p className="text-white/50 text-sm mb-6">Welcome back, adventurer.</p>

        <label className="block text-white/60 text-sm mb-1">Email</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="w-full rounded-xl bg-white/5 border border-white/10 focus:border-realm-primary outline-none px-4 py-2.5 text-white mb-4"
        />

        <label className="block text-white/60 text-sm mb-1">Password</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className="w-full rounded-xl bg-white/5 border border-white/10 focus:border-realm-primary outline-none px-4 py-2.5 text-white mb-4"
        />

        {login.isError && <p className="text-realm-danger text-sm mb-4">Invalid email or password.</p>}

        <button
          type="submit"
          disabled={login.isPending}
          className="w-full rounded-xl bg-realm-primary hover:bg-realm-primary/80 transition-colors px-4 py-2.5 text-white font-semibold disabled:opacity-50"
        >
          {login.isPending ? 'Signing in...' : 'Sign In'}
        </button>

        <p className="text-white/40 text-sm text-center mt-4">
          New here?{' '}
          <Link to="/register" className="text-realm-primary hover:underline">
            Create an account
          </Link>
        </p>
      </motion.form>
    </div>
  )
}
