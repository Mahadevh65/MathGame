/**
 * Reduced-motion counterparts for every reward/cinematic-tier animation.
 * These preserve 100% of the INFORMATION (you still see you leveled up,
 * unlocked a region, etc.) without the motion. Never hide state changes
 * because motion is disabled — see architecture Section 12.5.
 */
import { useEffect, useState } from 'react'

export function usePrefersReducedMotion(): boolean {
  const [reduced, setReduced] = useState(false)

  useEffect(() => {
    const query = window.matchMedia('(prefers-reduced-motion: reduce)')
    setReduced(query.matches)
    const listener = (e: MediaQueryListEvent) => setReduced(e.matches)
    query.addEventListener('change', listener)
    return () => query.removeEventListener('change', listener)
  }, [])

  return reduced
}

/** Instant, information-preserving fallback for reward-tier animations. */
export const reducedFade = {
  initial: { opacity: 0 },
  animate: { opacity: 1, transition: { duration: 0.15 } },
  exit: { opacity: 0, transition: { duration: 0.1 } }
}
