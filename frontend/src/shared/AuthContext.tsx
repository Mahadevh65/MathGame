import { createContext, useContext, useState, type ReactNode } from 'react'
import type { AuthResponse } from '../types'

interface AuthState {
  token: string | null
  displayName: string | null
  userId: string | null
  login: (auth: AuthResponse) => void
  logout: () => void
}

const AuthContext = createContext<AuthState | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(localStorage.getItem('tr_token'))
  const [displayName, setDisplayName] = useState<string | null>(localStorage.getItem('tr_display_name'))
  const [userId, setUserId] = useState<string | null>(localStorage.getItem('tr_user_id'))

  const login = (auth: AuthResponse) => {
    localStorage.setItem('tr_token', auth.token)
    localStorage.setItem('tr_display_name', auth.displayName)
    localStorage.setItem('tr_user_id', auth.userId)
    setToken(auth.token)
    setDisplayName(auth.displayName)
    setUserId(auth.userId)
  }

  const logout = () => {
    localStorage.removeItem('tr_token')
    localStorage.removeItem('tr_display_name')
    localStorage.removeItem('tr_user_id')
    setToken(null)
    setDisplayName(null)
    setUserId(null)
  }

  return <AuthContext.Provider value={{ token, displayName, userId, login, logout }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
