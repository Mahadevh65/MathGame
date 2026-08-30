package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.SkillMastery;
import com.thinkingrealms.backend.domain.ThinkingSkillMastery;
import com.thinkingrealms.backend.dto.student.RecommendationResponse;
import com.thinkingrealms.backend.repository.SkillMasteryRepository;
import com.thinkingrealms.backend.repository.ThinkingSkillMasteryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Transparent, rule-based recommendation engine (no ML in the MVP - see
 * architecture Section 9/10). Priority order:
 *   1. Any math topic mastery below 50% -> recommend review.
 *   2. Any thinking skill below 50% -> recommend a targeted thinking challenge.
 *   3. Otherwise -> recommend continuing to the next mission.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private static final double WEAK_THRESHOLD = 50.0;

    private final SkillMasteryRepository skillMasteryRepository;
    private final ThinkingSkillMasteryRepository thinkingSkillMasteryRepository;

    public RecommendationResponse recommend(UUID studentId) {
        List<SkillMastery> mathMastery = skillMasteryRepository.findByStudentId(studentId);

        Optional<SkillMastery> weakestMath = mathMastery.stream()
                .filter(m -> m.getMasteryPercent() < WEAK_THRESHOLD)
                .min(Comparator.comparingDouble(SkillMastery::getMasteryPercent));

        if (weakestMath.isPresent()) {
            SkillMastery m = weakestMath.get();
            return new RecommendationResponse(
                    "REVIEW_TOPIC",
                    "Review: " + m.getMathTopic().getName(),
                    "Your mastery here is " + Math.round(m.getMasteryPercent()) + "%. A quick review will make later missions much easier.",
                    m.getMathTopic().getSlug()
            );
        }

        List<ThinkingSkillMastery> thinkingMastery = thinkingSkillMasteryRepository.findByStudentId(studentId);
        Optional<ThinkingSkillMastery> weakestThinking = thinkingMastery.stream()
                .filter(m -> m.getMasteryPercent() < WEAK_THRESHOLD)
                .min(Comparator.comparingDouble(ThinkingSkillMastery::getMasteryPercent));

        if (weakestThinking.isPresent()) {
            ThinkingSkillMastery m = weakestThinking.get();
            return new RecommendationResponse(
                    "THINKING_CHALLENGE",
                    "Thinking Challenge: " + m.getThinkingSkillSlug().replace('-', ' '),
                    "This thinking skill is currently your weakest at " + Math.round(m.getMasteryPercent()) + "%.",
                    m.getThinkingSkillSlug()
            );
        }

        return new RecommendationResponse(
                "NEXT_MISSION",
                "Continue your journey",
                "You're doing well across the board — time for the next mission.",
                null
        );
    }
}
