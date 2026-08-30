package com.thinkingrealms.backend.dto.student;

public record RecommendationResponse(
        String type,        // "REVIEW_TOPIC" | "NEXT_MISSION" | "THINKING_CHALLENGE" | "BOSS_CHALLENGE"
        String title,
        String reason,
        String targetSlugOrId
) {}
