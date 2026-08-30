import { motion } from 'framer-motion'
import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useRegister } from '../../api/hooks'
import { useAuth } from '../../shared/AuthContext'
import { panelVariants } from '../../design-system/motion/motion'

export function RegisterPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [displayName, setDisplayName] = useState('')
  const register = useRegister()
  const { login: setAuth } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault()
    register.mutate(
      { email, password, displayName },
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
        <h1 className="text-2xl font-bold text-white mb-1">Begin your journey</h1>
        <p className="text-white/50 text-sm mb-6">Create an account to enter The Thinking Realms.</p>

        <label className="block text-white/60 text-sm mb-1">Display name</label>
        <input
          value={displayName}
          onChange={(e) => setDisplayName(e.target.value)}
          required
          className="w-full rounded-xl bg-white/5 border border-white/10 focus:border-realm-primary outline-none px-4 py-2.5 text-white mb-4"
        />

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
          minLength={8}
          className="w-full rounded-xl bg-white/5 border border-white/10 focus:border-realm-primary outline-none px-4 py-2.5 text-white mb-4"
        />

        {register.isError && (
          <p className="text-realm-danger text-sm mb-4">
            {(register.error as any)?.response?.data?.message ?? 'Registration failed.'}
          </p>
        )}

        <button
          type="submit"
          disabled={register.isPending}
          className="w-full rounded-xl bg-realm-primary hover:bg-realm-primary/80 transition-colors px-4 py-2.5 text-white font-semibold disabled:opacity-50"
        >
          {register.isPending ? 'Creating account...' : 'Create Account'}
        </button>

        <p className="text-white/40 text-sm text-center mt-4">
          Already have an account?{' '}
          <Link to="/login" className="text-realm-primary hover:underline">
            Sign in
          </Link>
        </p>
      </motion.form>
    </div>
  )
}
