package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.MathTopic;
import com.thinkingrealms.backend.domain.SkillMastery;
import com.thinkingrealms.backend.domain.ThinkingSkillMastery;
import com.thinkingrealms.backend.repository.SkillMasteryRepository;
import com.thinkingrealms.backend.repository.ThinkingSkillMasteryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Transparent, rule-based mastery engine (Section 9 of the architecture).
 * Uses an exponential-moving-average update rather than a black-box model,
 * so every mastery change can be explained to the student and to us.
 */
@Service
@RequiredArgsConstructor
public class MasteryService {

    private static final double LEARNING_RATE = 0.22;

    private final SkillMasteryRepository skillMasteryRepository;
    private final ThinkingSkillMasteryRepository thinkingSkillMasteryRepository;

    /** Returns the target mastery value implied by this single attempt. */
    private double targetFor(boolean correct, int hintsUsed) {
        if (!correct) return 0.0;
        return Math.max(55.0, 100.0 - (hintsUsed * 15.0));
    }

    public double updateMathMastery(UUID studentId, MathTopic topic, boolean correct, int hintsUsed) {
        SkillMastery mastery = skillMasteryRepository
                .findByStudentIdAndMathTopicId(studentId, topic.getId())
                .orElseGet(() -> {
                    SkillMastery m = new SkillMastery();
                    m.setStudentId(studentId);
                    m.setMathTopic(topic);
                    m.setMasteryPercent(0.0);
                    return m;
                });

        double target = targetFor(correct, hintsUsed);
        double updated = mastery.getMasteryPercent() + LEARNING_RATE * (target - mastery.getMasteryPercent());
        mastery.setMasteryPercent(clamp(updated));
        mastery.setAttemptsCount(mastery.getAttemptsCount() + 1);
        skillMasteryRepository.save(mastery);
        return mastery.getMasteryPercent();
    }

    public void updateThinkingMastery(UUID studentId, List<String> thinkingSkillSlugs, boolean correct, int hintsUsed) {
        double target = targetFor(correct, hintsUsed);

        for (String slug : thinkingSkillSlugs) {
            ThinkingSkillMastery mastery = thinkingSkillMasteryRepository
                    .findByStudentIdAndThinkingSkillSlug(studentId, slug)
                    .orElseGet(() -> {
                        ThinkingSkillMastery m = new ThinkingSkillMastery();
                        m.setStudentId(studentId);
                        m.setThinkingSkillSlug(slug);
                        m.setMasteryPercent(0.0);
                        return m;
                    });

            double updated = mastery.getMasteryPercent() + LEARNING_RATE * (target - mastery.getMasteryPercent());
            mastery.setMasteryPercent(clamp(updated));
            mastery.setAttemptsCount(mastery.getAttemptsCount() + 1);
            thinkingSkillMasteryRepository.save(mastery);
        }
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(100.0, value));
    }
}
