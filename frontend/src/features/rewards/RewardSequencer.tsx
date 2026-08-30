import { AnimatePresence, motion } from 'framer-motion'
import { useEffect, useState } from 'react'
import type { RewardEvent } from '../../types'
import { DURATION, EASE } from '../../design-system/motion/motion'
import { reducedFade, usePrefersReducedMotion } from '../../design-system/motion/reducedMotion'

/**
 * The single orchestrator for post-attempt feedback (architecture Section 13).
 *
 * Receives an ORDERED RewardEvent[] from the backend attempt response and
 * plays one overlay at a time, in order, rather than letting each event
 * animate independently and collide. This is what turns "a bunch of things
 * happened" into a single coherent moment.
 */
export function RewardSequencer({
  events,
  onComplete
}: {
  events: RewardEvent[]
  onComplete: () => void
}) {
  const [index, setIndex] = useState(0)
  const reducedMotion = usePrefersReducedMotion()

  const current = events[index]

  useEffect(() => {
    if (!current) {
      onComplete()
      return
    }

    const displayMs = displayDurationFor(current.type, reducedMotion) * 1000
    const timer = setTimeout(() => setIndex((i) => i + 1), displayMs)
    return () => clearTimeout(timer)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [index, events.length])

  if (!current) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center pointer-events-none">
      <AnimatePresence mode="wait">
        <motion.div
          key={`${current.type}-${index}`}
          {...(reducedMotion ? reducedFade : cardVariants)}
          className="pointer-events-auto"
        >
          <RewardCard event={current} />
        </motion.div>
      </AnimatePresence>
    </div>
  )
}

function displayDurationFor(type: RewardEvent['type'], reduced: boolean): number {
  if (reduced) return 0.9
  switch (type) {
    case 'xpGained':
      return DURATION.reward + 0.4
    case 'achievementUnlocked':
      return DURATION.cinematic * 0.75
    case 'levelUp':
      return DURATION.cinematic
    case 'regionUnlocked':
      return DURATION.cinematic
    default:
      return DURATION.reward
  }
}

const cardVariants = {
  initial: { opacity: 0, scale: 0.85, y: 20 },
  animate: { opacity: 1, scale: 1, y: 0, transition: { duration: DURATION.standard, ease: EASE.entrance } },
  exit: { opacity: 0, scale: 0.9, y: -10, transition: { duration: DURATION.micro, ease: EASE.exit } }
}

function RewardCard({ event }: { event: RewardEvent }) {
  switch (event.type) {
    case 'xpGained':
      return (
        <div className="rounded-2xl bg-realm-panel/90 backdrop-blur-md border border-realm-secondary/40 shadow-glowGreen px-8 py-5 text-center">
          <div className="text-realm-secondary text-sm uppercase tracking-widest mb-1">XP Gained</div>
          <div className="text-4xl font-bold text-white">+{String(event.payload.amount)} XP</div>
        </div>
      )
    case 'achievementUnlocked':
      return (
        <div className="rounded-2xl bg-realm-panel/90 backdrop-blur-md border border-realm-amber/50 shadow-glow px-8 py-6 text-center max-w-sm">
          <div className="text-realm-amber text-sm uppercase tracking-widest mb-2">Achievement Unlocked</div>
          <div className="text-2xl font-bold text-white mb-1">{String(event.payload.name)}</div>
          <div className="text-white/60 text-sm">{String(event.payload.description ?? '')}</div>
        </div>
      )
    case 'levelUp':
      return (
        <motion.div
          initial={{ rotateY: -90 }}
          animate={{ rotateY: 0 }}
          transition={{ duration: DURATION.standard, ease: EASE.entrance }}
          className="rounded-2xl bg-gradient-to-br from-realm-primary/90 to-realm-panel/95 border border-realm-primary/60 shadow-glow px-10 py-8 text-center"
        >
          <div className="text-white/80 text-sm uppercase tracking-widest mb-2">Level Up</div>
          <div className="text-5xl font-extrabold text-white">Level {String(event.payload.newLevel)}</div>
        </motion.div>
      )
    case 'regionUnlocked':
      return (
        <div className="rounded-2xl bg-realm-panel/90 backdrop-blur-md border border-realm-secondary/50 shadow-glowGreen px-10 py-8 text-center max-w-md">
          <div className="text-realm-secondary text-sm uppercase tracking-widest mb-2">New Region Unlocked</div>
          <div className="text-3xl font-bold text-white">{String(event.payload.regionName)}</div>
        </div>
      )
    default:
      return null
  }
}
