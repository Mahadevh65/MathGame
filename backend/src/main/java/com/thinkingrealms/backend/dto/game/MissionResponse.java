package com.thinkingrealms.backend.dto.game;

import com.thinkingrealms.backend.dto.question.QuestionResponse;

import java.util.List;
import java.util.UUID;

public record MissionResponse(
        UUID id,
        String name,
        String description,
        boolean isBoss,
        List<QuestionResponse> questions
) {}
