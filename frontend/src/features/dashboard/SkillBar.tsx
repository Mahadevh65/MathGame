import { motion } from 'framer-motion'
import { DURATION, EASE } from '../../design-system/motion/motion'

/**
 * A single animated mastery row, e.g. "Pattern Recognition ████████░░ 87%".
 * Bar fills with an eased animation on mount/update rather than snapping.
 */
export function SkillBar({ name, percent }: { name: string; percent: number }) {
  const rounded = Math.round(percent)

  return (
    <div className="mb-3">
      <div className="flex justify-between text-sm text-white/70 mb-1">
        <span className="capitalize">{name}</span>
        <span className="text-white/50">{rounded}%</span>
      </div>
      <div className="h-2.5 w-full rounded-full bg-white/10 overflow-hidden">
        <motion.div
          className="h-full rounded-full bg-gradient-to-r from-realm-primary to-realm-secondary"
          initial={{ width: 0 }}
          animate={{ width: `${rounded}%` }}
          transition={{ duration: DURATION.reward, ease: EASE.entrance }}
        />
      </div>
    </div>
  )
}
