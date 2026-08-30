package com.thinkingrealms.backend.dto.game;

import java.util.UUID;

public record MissionSummaryResponse(
        UUID id,
        String name,
        String description,
        boolean isBoss,
        int questionCount
) {}
