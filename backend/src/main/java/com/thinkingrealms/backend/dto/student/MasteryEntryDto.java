package com.thinkingrealms.backend.dto.student;

public record MasteryEntryDto(
        String slug,
        String name,
        double masteryPercent
) {}
