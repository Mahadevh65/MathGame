import { motion } from 'framer-motion'
import { useMemo } from 'react'
import { DURATION } from '../../design-system/motion/motion'

/**
 * 8-12 small particles that arc upward and fade, used on correct answers
 * (architecture Section 12.4). Implemented with plain Framer Motion divs
 * rather than a full particle library — cheap enough for this scale and
 * keeps the dependency footprint small.
 */
export function ParticleBurst({ count = 10 }: { count?: number }) {
  const particles = useMemo(
    () =>
      Array.from({ length: count }, (_, i) => ({
        id: i,
        angle: (Math.PI / (count - 1)) * i - Math.PI / 2,
        distance: 60 + Math.random() * 40,
        delay: Math.random() * 0.1
      })),
    [count]
  )

  return (
    <div className="absolute inset-0 pointer-events-none flex items-center justify-center">
      {particles.map((p) => {
        const dx = Math.cos(p.angle) * p.distance
        const dy = Math.sin(p.angle) * p.distance - 40 // bias upward toward XP counter
        return (
          <motion.span
            key={p.id}
            className="absolute h-2 w-2 rounded-full bg-realm-secondary shadow-glowGreen"
            initial={{ opacity: 1, x: 0, y: 0, scale: 0.6 }}
            animate={{ opacity: 0, x: dx, y: dy, scale: 1.1 }}
            transition={{ duration: DURATION.reward, delay: p.delay, ease: 'easeOut' }}
          />
        )
      })}
    </div>
  )
}
