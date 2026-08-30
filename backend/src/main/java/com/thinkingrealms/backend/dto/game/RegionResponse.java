package com.thinkingrealms.backend.dto.game;

import java.util.UUID;

public record RegionResponse(
        UUID id,
        String slug,
        String name,
        String description,
        int orderIndex,
        int unlockXpThreshold,
        String themeSlug,
        boolean unlocked
) {}
