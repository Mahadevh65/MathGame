package com.thinkingrealms.backend.dto.reward;

import java.util.Map;

/**
 * A single, ordered reward event. The frontend RewardSequencer consumes an
 * ordered list of these and plays the matching animation for each `type`.
 * type is one of: xpGained | achievementUnlocked | levelUp | regionUnlocked
 */
public record RewardEventDto(
        String type,
        Map<String, Object> payload
) {}
