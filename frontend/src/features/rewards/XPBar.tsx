import { motion } from 'framer-motion'
import { DURATION, EASE } from '../../design-system/motion/motion'

/**
 * XP progress bar: eases up with a slight overshoot-and-settle rather than
 * jumping instantly (architecture Section 12.4, "XP / Level Up").
 */
export function XPBar({ level, xpIntoLevel, xpNeeded }: { level: number; xpIntoLevel: number; xpNeeded: number }) {
  const percent = Math.min(100, (xpIntoLevel / xpNeeded) * 100)

  return (
    <div className="w-full">
      <div className="flex justify-between text-xs text-white/60 mb-1">
        <span>Level {level}</span>
        <span>
          {xpIntoLevel} / {xpNeeded} XP
        </span>
      </div>
      <div className="h-3 w-full rounded-full bg-white/10 overflow-hidden">
        <motion.div
          className="h-full rounded-full bg-gradient-to-r from-realm-primary to-realm-secondary"
          initial={{ width: 0 }}
          animate={{ width: [`${Math.min(100, percent + 6)}%`, `${percent}%`] }}
          transition={{ duration: DURATION.reward, ease: EASE.entrance, times: [0.7, 1] }}
        />
      </div>
    </div>
  )
}
