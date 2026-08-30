package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.Achievement;
import com.thinkingrealms.backend.domain.Attempt;
import com.thinkingrealms.backend.domain.StudentAchievement;
import com.thinkingrealms.backend.repository.AchievementRepository;
import com.thinkingrealms.backend.repository.AttemptRepository;
import com.thinkingrealms.backend.repository.StudentAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Rule-based achievement checks. Each rule is explicit and independently
 * testable rather than being buried inside AttemptService.
 */
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final StudentAchievementRepository studentAchievementRepository;
    private final AttemptRepository attemptRepository;

    /** Returns newly-earned achievements (empty if none), for this attempt. */
    public List<Achievement> evaluate(UUID studentId, boolean correctThisAttempt) {
        List<Achievement> newlyEarned = new ArrayList<>();

        if (correctThisAttempt) {
            maybeAward(studentId, "first_correct_answer").ifPresent(newlyEarned::add);

            List<Attempt> recent = attemptRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
            if (recent.size() >= 5) {
                boolean lastFiveCorrect = recent.subList(0, 5).stream().allMatch(Attempt::isCorrect);
                if (lastFiveCorrect) {
                    maybeAward(studentId, "five_streak").ifPresent(newlyEarned::add);
                }
            }
        }

        return newlyEarned;
    }

    private java.util.Optional<Achievement> maybeAward(UUID studentId, String code) {
        Achievement achievement = achievementRepository.findByCode(code).orElse(null);
        if (achievement == null) return java.util.Optional.empty();

        if (studentAchievementRepository.existsByStudentIdAndAchievementId(studentId, achievement.getId())) {
            return java.util.Optional.empty();
        }

        StudentAchievement earned = new StudentAchievement();
        earned.setStudentId(studentId);
        earned.setAchievement(achievement);
        studentAchievementRepository.save(earned);

        return java.util.Optional.of(achievement);
    }
}
