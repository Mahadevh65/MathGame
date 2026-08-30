package com.thinkingrealms.backend.dto.question;

public record HintResponse(
        int hintIndex,
        String hintText,
        int hintsRemaining
) {}
