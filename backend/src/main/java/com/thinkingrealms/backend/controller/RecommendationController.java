package com.thinkingrealms.backend.controller;

import com.thinkingrealms.backend.dto.student.RecommendationResponse;
import com.thinkingrealms.backend.security.CurrentUser;
import com.thinkingrealms.backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendation() {
        return ResponseEntity.ok(recommendationService.recommend(CurrentUser.id()));
    }
}
