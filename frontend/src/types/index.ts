export interface AuthResponse {
  token: string
  userId: string
  displayName: string
  role: string
}

export interface MasteryEntry {
  slug: string
  name: string
  masteryPercent: number
}

export interface ProgressResponse {
  totalXp: number
  level: number
  xpIntoCurrentLevel: number
  xpNeededForNextLevel: number
  currentRegionId: string | null
  currentMissionId: string | null
  mathMastery: MasteryEntry[]
}

export interface ThinkingProfileResponse {
  thinkingSkills: MasteryEntry[]
}

export interface RegionDto {
  id: string
  slug: string
  name: string
  description: string
  orderIndex: number
  unlockXpThreshold: number
  themeSlug: string
  unlocked: boolean
}

export interface WorldDto {
  id: string
  slug: string
  name: string
  description: string
  regions: RegionDto[]
}

export interface QuestionDto {
  id: string
  questionText: string
  questionType: string
  difficulty: number
  mathTopicSlug: string
  thinkingSkillSlugs: string[]
  expectedTimeSeconds: number
  hintsAvailable: number
}

export interface MissionSummaryDto {
  id: string
  name: string
  description: string
  isBoss: boolean
  questionCount: number
}

export interface MissionDto {
  id: string
  name: string
  description: string
  isBoss: boolean
  questions: QuestionDto[]
}

export interface RewardEvent {
  type: 'xpGained' | 'achievementUnlocked' | 'levelUp' | 'regionUnlocked' | 'rewardGranted'
  payload: Record<string, unknown>
}

export interface AttemptResult {
  correct: boolean
  correctAnswer: string
  explanation: string
  commonMistakeNote: string | null
  rewardEvents: RewardEvent[]
}

export interface RecommendationDto {
  type: 'REVIEW_TOPIC' | 'NEXT_MISSION' | 'THINKING_CHALLENGE' | 'BOSS_CHALLENGE'
  title: string
  reason: string
  targetSlugOrId: string | null
}
