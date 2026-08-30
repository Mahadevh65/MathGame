/**
 * Central motion design system for The Thinking Realms.
 *
 * Every animated component in the app should import its durations and
 * easings from here rather than inventing its own. This is what keeps
 * the whole product feeling like one coherent game instead of a pile
 * of one-off effects (see architecture Section 12).
 */

export const DURATION = {
  instant: 0.1, // button press, toggle
  micro: 0.2, // hover, tap feedback, icon transitions
  standard: 0.4, // panel open, card flip, route change
  reward: 0.9, // XP burst, correct-answer celebration
  cinematic: 1.6 // world unlock, boss victory, level up
} as const

export const EASE = {
  entrance: [0.16, 1, 0.3, 1] as const,
  exit: [0.7, 0, 0.84, 0] as const,
  linear: 'linear' as const
}

export const SPRING_BOUNCE = {
  type: 'spring' as const,
  stiffness: 300,
  damping: 20
}

/** Standard entrance transition for panels, cards, route content. */
export const enter = {
  transition: { duration: DURATION.standard, ease: EASE.entrance }
}

/** Standard exit transition. */
export const exit = {
  transition: { duration: DURATION.standard, ease: EASE.exit }
}

/** Micro-interaction transition for hover/tap. */
export const micro = {
  transition: { duration: DURATION.micro, ease: EASE.entrance }
}

/** Reusable Framer Motion variants for a panel/card that slides + fades in. */
export const panelVariants = {
  initial: { opacity: 0, y: 16 },
  animate: { opacity: 1, y: 0, transition: { duration: DURATION.standard, ease: EASE.entrance } },
  exit: { opacity: 0, y: -12, transition: { duration: DURATION.standard, ease: EASE.exit } }
}

/** Correct-answer scale-bounce: 1 -> 1.08 -> 1 */
export const correctAnswerScale = {
  scale: [1, 1.08, 1],
  transition: { duration: DURATION.micro * 1.5, ease: EASE.entrance }
}

/** Wrong-answer gentle shake: 2-3px, 2 cycles, ~150ms — never aggressive.
 *  Explicitly asserts opacity/y so this state doesn't silently inherit the
 *  invisible `initial` values from panelVariants (opacity: 0, y: 16). */
export const wrongAnswerShake = {
  opacity: 1,
  y: 0,
  x: [0, -3, 3, -2, 2, 0],
  transition: { duration: 0.15 * 2, ease: 'easeInOut' }
}

/** World/region unlock camera-style zoom used on the game map. */
export const regionUnlockZoom = {
  scale: [1, 1.04, 1],
  transition: { duration: DURATION.cinematic, ease: EASE.entrance }
}

/** Boss panel rising from below on entry. */
export const bossPanelEnter = {
  initial: { opacity: 0, y: 60 },
  animate: { opacity: 1, y: 0, transition: { duration: DURATION.cinematic * 0.6, ease: EASE.entrance } },
  exit: { opacity: 0, y: 40, transition: { duration: DURATION.standard, ease: EASE.exit } }
}

export type MotionEventType =
  | 'xpGained'
  | 'achievementUnlocked'
  | 'levelUp'
  | 'regionUnlocked'
  | 'rewardGranted'
