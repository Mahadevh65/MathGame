package com.thinkingrealms.backend.controller;

import com.thinkingrealms.backend.dto.question.HintResponse;
import com.thinkingrealms.backend.dto.question.QuestionResponse;
import com.thinkingrealms.backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable UUID id) {
        return ResponseEntity.ok(questionService.getQuestion(id));
    }

    @GetMapping("/{id}/hints/{hintIndex}")
    public ResponseEntity<HintResponse> getHint(@PathVariable UUID id, @PathVariable int hintIndex) {
        return ResponseEntity.ok(questionService.getHint(id, hintIndex));
    }
}
