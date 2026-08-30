import { motion } from 'framer-motion'
import type { RegionDto } from '../../types'
import { DURATION, EASE } from '../../design-system/motion/motion'

/**
 * A single region node on the world map. Locked regions render behind a
 * fog-of-war overlay; unlocked regions idle-glow to invite exploration
 * (architecture Section 12.4, "World / Region Unlock").
 */
export function RegionNode({ region, onEnter }: { region: RegionDto; onEnter: () => void }) {
  return (
    <motion.button
      onClick={region.unlocked ? onEnter : undefined}
      whileHover={region.unlocked ? { scale: 1.03 } : {}}
      whileTap={region.unlocked ? { scale: 0.98 } : {}}
      className={`relative rounded-2xl border p-6 text-left overflow-hidden transition-colors ${
        region.unlocked
          ? 'border-realm-secondary/40 bg-realm-panel/70 cursor-pointer'
          : 'border-white/10 bg-realm-panel/40 cursor-not-allowed'
      }`}
    >
      {region.unlocked && (
        <motion.div
          className="absolute inset-0 bg-realm-secondary/5"
          animate={{ opacity: [0.15, 0.3, 0.15] }}
          transition={{ duration: DURATION.cinematic, repeat: Infinity, ease: 'easeInOut' }}
        />
      )}

      {!region.unlocked && (
        <motion.div
          initial={{ opacity: 1 }}
          className="absolute inset-0 backdrop-blur-sm bg-black/40 flex items-center justify-center"
        >
          <div className="text-white/50 text-xs uppercase tracking-widest">
            Unlocks at {region.unlockXpThreshold} XP
          </div>
        </motion.div>
      )}

      <div className="relative z-10">
        <div className="text-xs uppercase tracking-widest text-white/40 mb-1">Region {region.orderIndex + 1}</div>
        <h3 className="text-xl font-bold text-white mb-2">{region.name}</h3>
        <p className="text-white/60 text-sm">{region.description}</p>
      </div>
    </motion.button>
  )
}

export const regionRevealTransition = { duration: DURATION.cinematic, ease: EASE.entrance }
