import { AnimatePresence, motion } from 'framer-motion'
import { useState } from 'react'
import type { QuestionDto } from '../../types'
import { useHint, useSubmitAttempt } from '../../api/hooks'
import { correctAnswerScale, panelVariants, wrongAnswerShake } from '../../design-system/motion/motion'
import { ParticleBurst } from './ParticleBurst'
import { RewardSequencer } from '../rewards/RewardSequencer'
import type { RewardEvent } from '../../types'

type FeedbackState = 'idle' | 'correct' | 'wrong'

export interface AnsweredResult {
  submittedAnswer: string
  correctAnswer: string
  explanation: string
}

export function QuestionCard({
  question,
  onAnswered
}: {
  question: QuestionDto
  onAnswered: (result: AnsweredResult) => void
}) {
  const [answer, setAnswer] = useState('')
  const [feedback, setFeedback] = useState<FeedbackState>('idle')
  const [hintsShown, setHintsShown] = useState<string[]>([])
  const [explanation, setExplanation] = useState<string | null>(null)
  const [mistakeNote, setMistakeNote] = useState<string | null>(null)
  const [correctAnswerText, setCorrectAnswerText] = useState('')
  const [rewardQueue, setRewardQueue] = useState<RewardEvent[] | null>(null)
  const [startTime] = useState(() => Date.now())

  const submitAttempt = useSubmitAttempt()
  const hintMutation = useHint()

  const handleSubmit = () => {
    if (!answer.trim() || submitAttempt.isPending) return

    submitAttempt.mutate(
      {
        questionId: question.id,
        submittedAnswer: answer,
        hintsUsed: hintsShown.length,
        timeTakenSeconds: Math.round((Date.now() - startTime) / 1000)
      },
      {
        onSuccess: (result) => {
          setFeedback(result.correct ? 'correct' : 'wrong')
          setExplanation(result.explanation)
          setMistakeNote(result.commonMistakeNote)
          setCorrectAnswerText(result.correctAnswer)
          if (result.correct && result.rewardEvents.length > 0) {
            setRewardQueue(result.rewardEvents)
          }
        }
      }
    )
  }

  const handleHint = () => {
    if (hintsShown.length >= question.hintsAvailable) return
    hintMutation.mutate(
      { questionId: question.id, hintIndex: hintsShown.length },
      {
        onSuccess: (res) => setHintsShown((prev) => [...prev, res.hintText])
      }
    )
  }

  /** Wrong answer -> let the student try the same question again. Hints already
   *  revealed stay visible; nothing advances until they get it right. */
  const handleTryAgain = () => {
    setAnswer('')
    setFeedback('idle')
    setExplanation(null)
    setMistakeNote(null)
  }

  /** Correct answer -> hand the result up to PracticePage, which advances
   *  the mission and records this question into the review history. */
  const handleContinueAfterCorrect = () => {
    onAnswered({
      submittedAnswer: answer,
      correctAnswer: correctAnswerText,
      explanation: explanation ?? ''
    })
  }

  return (
    <>
      {rewardQueue && <RewardSequencer events={rewardQueue} onComplete={() => setRewardQueue(null)} />}

      <motion.div
        variants={panelVariants}
        initial="initial"
        animate={feedback === 'wrong' ? wrongAnswerShake : 'animate'}
        exit="exit"
        className="relative rounded-2xl bg-realm-panel/80 backdrop-blur-md border border-white/10 p-8 max-w-2xl w-full"
      >
        {feedback === 'correct' && <ParticleBurst />}

        <div className="text-xs uppercase tracking-widest text-realm-primary mb-2">
          {question.mathTopicSlug.replace('-', ' ')} · Difficulty {question.difficulty}
        </div>

        <motion.p
          animate={feedback === 'correct' ? correctAnswerScale : {}}
          className="text-xl text-white font-medium mb-6 leading-relaxed"
        >
          {question.questionText}
        </motion.p>

        {feedback === 'idle' && (
          <>
            <input
              value={answer}
              onChange={(e) => setAnswer(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSubmit()}
              placeholder="Type your answer..."
              className="w-full rounded-xl bg-white/5 border border-white/10 focus:border-realm-primary outline-none px-4 py-3 text-white placeholder-white/30 mb-4"
            />

            <div className="flex items-center justify-between gap-3">
              <button
                onClick={handleHint}
                disabled={hintsShown.length >= question.hintsAvailable}
                className="text-sm text-white/60 hover:text-realm-amber transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
              >
                Hint ({question.hintsAvailable - hintsShown.length} left)
              </button>
              <button
                onClick={handleSubmit}
                disabled={!answer.trim() || submitAttempt.isPending}
                className="rounded-xl bg-realm-primary hover:bg-realm-primary/80 transition-colors px-6 py-2.5 text-white font-semibold disabled:opacity-40"
              >
                {submitAttempt.isPending ? 'Checking...' : 'Submit'}
              </button>
            </div>

            <AnimatePresence>
              {hintsShown.map((hint, i) => (
                <motion.div
                  key={i}
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  className="mt-3 text-sm text-realm-amber bg-realm-amber/10 rounded-lg px-4 py-2"
                >
                  Hint {i + 1}: {hint}
                </motion.div>
              ))}
            </AnimatePresence>
          </>
        )}

        {feedback !== 'idle' && (
          <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.15 }}>
            {feedback === 'wrong' && (
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.2 }}
                className="rounded-xl bg-realm-amber/10 border border-realm-amber/30 px-4 py-3 mb-4"
              >
                <div className="text-realm-amber font-semibold mb-1">Let's look at this together</div>
                {mistakeNote && <div className="text-white/70 text-sm">{mistakeNote}</div>}
              </motion.div>
            )}

            {feedback === 'correct' && (
              <div className="rounded-xl bg-realm-secondary/10 border border-realm-secondary/30 px-4 py-3 mb-4">
                <div className="text-realm-secondary font-semibold">Correct!</div>
              </div>
            )}

            {explanation && <p className="text-white/70 text-sm mb-6">{explanation}</p>}

            {feedback === 'wrong' ? (
              <button
                onClick={handleTryAgain}
                className="rounded-xl bg-realm-amber/20 hover:bg-realm-amber/30 transition-colors px-6 py-2.5 text-realm-amber font-semibold"
              >
                Try Again
              </button>
            ) : (
              <button
                onClick={handleContinueAfterCorrect}
                className="rounded-xl bg-white/10 hover:bg-white/20 transition-colors px-6 py-2.5 text-white font-semibold"
              >
                Continue
              </button>
            )}
          </motion.div>
        )}
      </motion.div>
    </>
  )
}
