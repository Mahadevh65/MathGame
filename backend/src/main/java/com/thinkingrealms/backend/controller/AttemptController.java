package com.thinkingrealms.backend.controller;

import com.thinkingrealms.backend.dto.question.AttemptRequest;
import com.thinkingrealms.backend.dto.question.AttemptResultResponse;
import com.thinkingrealms.backend.security.CurrentUser;
import com.thinkingrealms.backend.service.AttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;

    @PostMapping("/{id}/attempt")
    public ResponseEntity<AttemptResultResponse> submitAttempt(
            @PathVariable("id") UUID questionId,
            @Valid @RequestBody AttemptRequest request
    ) {
        UUID studentId = CurrentUser.id();
        return ResponseEntity.ok(attemptService.submitAttempt(studentId, questionId, request));
    }
}
