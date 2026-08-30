package com.thinkingrealms.backend.dto.game;

import java.util.List;
import java.util.UUID;

public record WorldResponse(
        UUID id,
        String slug,
        String name,
        String description,
        List<RegionResponse> regions
) {}
