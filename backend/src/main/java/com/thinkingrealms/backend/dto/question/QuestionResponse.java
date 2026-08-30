package com.thinkingrealms.backend.dto.question;

import java.util.List;
import java.util.UUID;

public record QuestionResponse(
        UUID id,
        String questionText,
        String questionType,
        int difficulty,
        String mathTopicSlug,
        List<String> thinkingSkillSlugs,
        int expectedTimeSeconds,
        int hintsAvailable
) {}
