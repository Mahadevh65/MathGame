package com.thinkingrealms.backend.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public record AttemptRequest(
        @NotBlank String submittedAnswer,
        @Min(0) int hintsUsed,
        @Min(0) int timeTakenSeconds
) {}
