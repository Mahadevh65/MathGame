package com.thinkingrealms.backend.dto.question;

import com.thinkingrealms.backend.dto.reward.RewardEventDto;

import java.util.List;

public record AttemptResultResponse(
        boolean correct,
        String correctAnswer,
        String explanation,
        String commonMistakeNote,
        List<RewardEventDto> rewardEvents
) {}
