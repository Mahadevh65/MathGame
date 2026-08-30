import { AnimatePresence, motion } from 'framer-motion'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useRegionMissions, useWorlds } from '../../api/hooks'
import { RegionNode } from './RegionNode'
import { panelVariants } from '../../design-system/motion/motion'
import type { RegionDto } from '../../types'

export function GameMapPage() {
  const { data: worlds, isLoading } = useWorlds()
  const [selectedRegion, setSelectedRegion] = useState<RegionDto | null>(null)
  const { data: missions } = useRegionMissions(selectedRegion?.id)
  const navigate = useNavigate()

  if (isLoading || !worlds || worlds.length === 0) {
    return <div className="text-white/60 p-10">Loading world...</div>
  }

  const world = worlds[0]

  return (
    <div className="min-h-screen bg-realm-void p-6 md:p-10">
      <div className="max-w-4xl mx-auto">
        <button onClick={() => navigate('/dashboard')} className="text-white/40 hover:text-white/70 text-sm mb-6">
          ← Back to dashboard
        </button>

        <h1 className="text-3xl font-bold text-white mb-1">{world.name}</h1>
        <p className="text-white/50 mb-8">{world.description}</p>

        <div className="grid md:grid-cols-2 gap-5 mb-8">
          {world.regions.map((region) => (
            <RegionNode key={region.id} region={region} onEnter={() => setSelectedRegion(region)} />
          ))}
        </div>

        <AnimatePresence>
          {selectedRegion && (
            <motion.div
              variants={panelVariants}
              initial="initial"
              animate="animate"
              exit="exit"
              className="rounded-2xl bg-realm-panel/70 border border-white/10 p-6"
            >
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-bold text-white">{selectedRegion.name} — Missions</h2>
                <button onClick={() => setSelectedRegion(null)} className="text-white/40 hover:text-white/70 text-sm">
                  Close
                </button>
              </div>

              <div className="space-y-3">
                {missions?.map((mission) => (
                  <motion.button
                    key={mission.id}
                    whileHover={{ x: 4 }}
                    onClick={() => navigate(`/practice/${mission.id}`)}
                    className={`w-full flex items-center justify-between rounded-xl border px-5 py-4 text-left transition-colors ${
                      mission.isBoss
                        ? 'border-realm-amber/40 bg-realm-amber/5 hover:bg-realm-amber/10'
                        : 'border-white/10 bg-white/5 hover:bg-white/10'
                    }`}
                  >
                    <div>
                      <div className={`font-semibold ${mission.isBoss ? 'text-realm-amber' : 'text-white'}`}>
                        {mission.isBoss ? '⚔ ' : ''}
                        {mission.name}
                      </div>
                      <div className="text-white/50 text-sm">{mission.description}</div>
                    </div>
                    <div className="text-white/30 text-xs">{mission.questionCount} questions</div>
                  </motion.button>
                ))}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  )
}
