import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import { useProgress, useRecommendation, useThinkingProfile } from '../../api/hooks'
import { useAuth } from '../../shared/AuthContext'
import { XPBar } from '../rewards/XPBar'
import { SkillBar } from './SkillBar'
import { panelVariants } from '../../design-system/motion/motion'

export function DashboardPage() {
  const { displayName, logout } = useAuth()
  const { data: progress, isLoading: progressLoading } = useProgress()
  const { data: thinking, isLoading: thinkingLoading } = useThinkingProfile()
  const { data: recommendation } = useRecommendation()

  return (
    <div className="min-h-screen bg-realm-void p-6 md:p-10">
      <div className="max-w-4xl mx-auto">
        <header className="flex items-center justify-between mb-8">
          <div>
            <div className="text-white/50 text-sm">Welcome back,</div>
            <h1 className="text-2xl font-bold text-white">{displayName}</h1>
          </div>
          <div className="flex gap-3">
            <Link
              to="/game"
              className="rounded-xl bg-realm-primary hover:bg-realm-primary/80 transition-colors px-5 py-2.5 text-white font-semibold"
            >
              Enter The Thinking Realms
            </Link>
            <button onClick={logout} className="text-white/40 hover:text-white/70 transition-colors text-sm px-2">
              Log out
            </button>
          </div>
        </header>

        {progress && !progressLoading && (
          <motion.div variants={panelVariants} initial="initial" animate="animate" className="rounded-2xl bg-realm-panel/70 border border-white/10 p-6 mb-6">
            <XPBar level={progress.level} xpIntoLevel={progress.xpIntoCurrentLevel} xpNeeded={progress.xpNeededForNextLevel} />
            <div className="text-white/40 text-xs mt-2">{progress.totalXp} total XP earned</div>
          </motion.div>
        )}

        {recommendation && (
          <motion.div
            variants={panelVariants}
            initial="initial"
            animate="animate"
            transition={{ delay: 0.05 }}
            className="rounded-2xl bg-realm-primary/10 border border-realm-primary/30 p-6 mb-8"
          >
            <div className="text-realm-primary text-xs uppercase tracking-widest mb-1">Recommended for you</div>
            <div className="text-white font-semibold text-lg mb-1">{recommendation.title}</div>
            <div className="text-white/60 text-sm">{recommendation.reason}</div>
          </motion.div>
        )}

        <div className="grid md:grid-cols-2 gap-6">
          <motion.div variants={panelVariants} initial="initial" animate="animate" transition={{ delay: 0.1 }} className="rounded-2xl bg-realm-panel/70 border border-white/10 p-6">
            <h2 className="text-white font-semibold mb-4">Mathematics Mastery</h2>
            {progress && progress.mathMastery.length > 0 ? (
              progress.mathMastery.map((m) => <SkillBar key={m.slug} name={m.name} percent={m.masteryPercent} />)
            ) : (
              <p className="text-white/40 text-sm">Complete a few questions to see your mastery here.</p>
            )}
          </motion.div>

          <motion.div variants={panelVariants} initial="initial" animate="animate" transition={{ delay: 0.15 }} className="rounded-2xl bg-realm-panel/70 border border-white/10 p-6">
            <h2 className="text-white font-semibold mb-4">Your Thinking Journey</h2>
            {thinking && !thinkingLoading && thinking.thinkingSkills.length > 0 ? (
              thinking.thinkingSkills.map((s) => <SkillBar key={s.slug} name={s.name} percent={s.masteryPercent} />)
            ) : (
              <p className="text-white/40 text-sm">Complete a few questions to see your thinking profile here.</p>
            )}
          </motion.div>
        </div>
      </div>
    </div>
  )
}
