package com.thinkingrealms.backend.dto.student;

import java.util.List;
import java.util.UUID;

public record ProgressResponse(
        int totalXp,
        int level,
        int xpIntoCurrentLevel,
        int xpNeededForNextLevel,
        UUID currentRegionId,
        UUID currentMissionId,
        List<MasteryEntryDto> mathMastery
) {}
