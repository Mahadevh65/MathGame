import { AnimatePresence, motion } from 'framer-motion'
import { useEffect, useState, type ReactNode } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMission } from '../../api/hooks'
import { QuestionCard, type AnsweredResult } from './QuestionCard'
import { panelVariants } from '../../design-system/motion/motion'

interface AnsweredEntry {
  questionText: string
  submittedAnswer: string
  correctAnswer: string
  explanation: string
}

export function PracticePage() {
  const { missionId } = useParams<{ missionId: string }>()
  const { data: mission, isLoading } = useMission(missionId)
  const navigate = useNavigate()

  // Furthest index the student has actually reached (only advances on a correct answer).
  const [questionIndex, setQuestionIndex] = useState(() => {
    const saved = localStorage.getItem(`tr_mission_progress_${missionId}`)
    return saved ? Number(saved) : 0
  })

  // Index currently being displayed - can move backward into history via "Previous"
  // without disturbing actual progress.
  const [viewIndex, setViewIndex] = useState(questionIndex)

  // In-memory history of answered questions, for the review view. Not persisted
  // across refresh in this version.
  const [history, setHistory] = useState<Record<number, AnsweredEntry>>({})

  useEffect(() => {
    if (missionId) {
      localStorage.setItem(`tr_mission_progress_${missionId}`, String(questionIndex))
    }
  }, [questionIndex, missionId])

  if (isLoading || !mission) {
    return <div className="text-white/60 p-10">Loading mission...</div>
  }

  const missionComplete = questionIndex >= mission.questions.length
  const viewingHistory = viewIndex < questionIndex

  const handleAnswered = (result: AnsweredResult) => {
    setHistory((prev) => ({
      ...prev,
      [questionIndex]: {
        questionText: mission.questions[questionIndex].questionText,
        ...result
      }
    }))
    const next = questionIndex + 1
    setQuestionIndex(next)
    setViewIndex(next)
  }

  const handleReturnToMap = () => {
    if (missionId) {
      localStorage.removeItem(`tr_mission_progress_${missionId}`)
    }
    navigate('/game')
  }

  const goPrevious = () => setViewIndex((i) => Math.max(0, i - 1))
  const goNext = () => setViewIndex((i) => Math.min(questionIndex, i + 1))

  const content = (
    <>
      <div className="w-full max-w-2xl mb-6">
        <div className="flex items-center justify-between mb-2">
          <div className="text-white/50 text-sm">{mission.name}</div>
          <div className="flex gap-2">
            <button
              onClick={goPrevious}
              disabled={viewIndex === 0}
              className="text-xs text-white/50 hover:text-white disabled:opacity-30 disabled:cursor-not-allowed px-3 py-1 rounded-lg bg-white/5"
            >
              ← Previous
            </button>
            <button
              onClick={goNext}
              disabled={viewIndex >= questionIndex}
              className="text-xs text-white/50 hover:text-white disabled:opacity-30 disabled:cursor-not-allowed px-3 py-1 rounded-lg bg-white/5"
            >
              Next →
            </button>
          </div>
        </div>
        <ProgressDots total={mission.questions.length} current={questionIndex} viewing={viewIndex} />
      </div>

      <AnimatePresence mode="wait">
        {missionComplete && viewIndex >= mission.questions.length ? (
          <motion.div
            key="complete"
            variants={panelVariants}
            initial="initial"
            animate="animate"
            className="rounded-2xl bg-realm-panel/80 border border-realm-secondary/30 p-10 text-center max-w-lg"
          >
            <div className="text-realm-secondary text-sm uppercase tracking-widest mb-2">Mission Complete</div>
            <h2 className="text-3xl font-bold text-white mb-6">{mission.name}</h2>
            <button
              onClick={handleReturnToMap}
              className="rounded-xl bg-realm-primary hover:bg-realm-primary/80 transition-colors px-6 py-2.5 text-white font-semibold"
            >
              Return to Map
            </button>
          </motion.div>
        ) : viewingHistory ? (
          <ReviewCard key={`review-${viewIndex}`} entry={history[viewIndex]} />
        ) : (
          <QuestionCard
            key={mission.questions[questionIndex].id}
            question={mission.questions[questionIndex]}
            onAnswered={handleAnswered}
          />
        )}
      </AnimatePresence>
    </>
  )

  if (mission.isBoss && !missionComplete) {
    return <BossWrapper missionName={mission.name}>{content}</BossWrapper>
  }

  return (
    <div className="min-h-screen bg-realm-void flex flex-col items-center justify-center p-6">{content}</div>
  )
}

function ReviewCard({ entry }: { entry: AnsweredEntry | undefined }) {
  if (!entry) return null
  return (
    <motion.div
      variants={panelVariants}
      initial="initial"
      animate="animate"
      exit="exit"
      className="relative rounded-2xl bg-realm-panel/60 backdrop-blur-md border border-white/10 p-8 max-w-2xl w-full"
    >
      <div className="text-xs uppercase tracking-widest text-realm-secondary mb-2">Already answered</div>
      <p className="text-xl text-white font-medium mb-6 leading-relaxed">{entry.questionText}</p>
      <div className="rounded-xl bg-realm-secondary/10 border border-realm-secondary/30 px-4 py-3 mb-4">
        <div className="text-white/60 text-sm mb-1">Your answer</div>
        <div className="text-white font-semibold">{entry.submittedAnswer}</div>
      </div>
      {entry.explanation && <p className="text-white/70 text-sm">{entry.explanation}</p>}
    </motion.div>
  )
}

function ProgressDots({ total, current, viewing }: { total: number; current: number; viewing: number }) {
  return (
    <div className="flex gap-2">
      {Array.from({ length: total }).map((_, i) => (
        <div
          key={i}
          className={`h-1.5 flex-1 rounded-full transition-colors duration-300 ${
            i === viewing
              ? 'bg-white'
              : i < current
              ? 'bg-realm-secondary'
              : i === current
              ? 'bg-realm-primary'
              : 'bg-white/10'
          }`}
        />
      ))}
    </div>
  )
}

/**
 * Boss entry treatment (architecture Section 12.4): UI chrome fades away,
 * background becomes more dramatic, question panel rises from below.
 */
function BossWrapper({ missionName, children }: { missionName: string; children: ReactNode }) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.6 }}
      className="min-h-screen flex flex-col items-center justify-center p-6 bg-gradient-to-b from-[#1a0f2e] via-realm-void to-[#0a0612]"
    >
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-realm-amber text-sm uppercase tracking-[0.3em] mb-8"
      >
        Boss Challenge · {missionName}
      </motion.div>
      <motion.div
        initial={{ opacity: 0, y: 60 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.9, ease: [0.16, 1, 0.3, 1] }}
        className="w-full max-w-2xl"
      >
        {children}
      </motion.div>
    </motion.div>
  )
}
