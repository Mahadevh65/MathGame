import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { apiClient } from './client'
import type {
  AttemptResult,
  AuthResponse,
  MissionDto,
  MissionSummaryDto,
  ProgressResponse,
  RecommendationDto,
  ThinkingProfileResponse,
  WorldDto
} from '../types'

export function useLogin() {
  return useMutation({
    mutationFn: async (input: { email: string; password: string }) => {
      const { data } = await apiClient.post<AuthResponse>('/auth/login', input)
      return data
    }
  })
}

export function useRegister() {
  return useMutation({
    mutationFn: async (input: { email: string; password: string; displayName: string }) => {
      const { data } = await apiClient.post<AuthResponse>('/auth/register', input)
      return data
    }
  })
}

export function useProgress() {
  return useQuery({
    queryKey: ['progress'],
    queryFn: async () => {
      const { data } = await apiClient.get<ProgressResponse>('/students/me/progress')
      return data
    }
  })
}

export function useThinkingProfile() {
  return useQuery({
    queryKey: ['thinking-profile'],
    queryFn: async () => {
      const { data } = await apiClient.get<ThinkingProfileResponse>('/students/me/thinking-profile')
      return data
    }
  })
}

export function useWorlds() {
  return useQuery({
    queryKey: ['worlds'],
    queryFn: async () => {
      const { data } = await apiClient.get<WorldDto[]>('/game/worlds')
      return data
    }
  })
}

export function useRegionMissions(regionId: string | undefined) {
  return useQuery({
    queryKey: ['region-missions', regionId],
    queryFn: async () => {
      const { data } = await apiClient.get<MissionSummaryDto[]>(`/game/regions/${regionId}/missions`)
      return data
    },
    enabled: !!regionId
  })
}

export function useMission(missionId: string | undefined) {
  return useQuery({
    queryKey: ['mission', missionId],
    queryFn: async () => {
      const { data } = await apiClient.get<MissionDto>(`/game/missions/${missionId}`)
      return data
    },
    enabled: !!missionId
  })
}

export function useRecommendation() {
  return useQuery({
    queryKey: ['recommendation'],
    queryFn: async () => {
      const { data } = await apiClient.get<RecommendationDto>('/recommendations')
      return data
    }
  })
}

export function useSubmitAttempt() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (input: { questionId: string; submittedAnswer: string; hintsUsed: number; timeTakenSeconds: number }) => {
      const { data } = await apiClient.post<AttemptResult>(`/questions/${input.questionId}/attempt`, {
        submittedAnswer: input.submittedAnswer,
        hintsUsed: input.hintsUsed,
        timeTakenSeconds: input.timeTakenSeconds
      })
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['progress'] })
      queryClient.invalidateQueries({ queryKey: ['thinking-profile'] })
      queryClient.invalidateQueries({ queryKey: ['worlds'] })
      queryClient.invalidateQueries({ queryKey: ['recommendation'] })
    }
  })
}

export function useHint() {
  return useMutation({
    mutationFn: async (input: { questionId: string; hintIndex: number }) => {
      const { data } = await apiClient.get(`/questions/${input.questionId}/hints/${input.hintIndex}`)
      return data as { hintIndex: number; hintText: string; hintsRemaining: number }
    }
  })
}
