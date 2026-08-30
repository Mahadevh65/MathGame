package com.thinkingrealms.backend.dto.student;

import java.util.List;

public record ThinkingProfileResponse(
        List<MasteryEntryDto> thinkingSkills
) {}
