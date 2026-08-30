package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.*;
import com.thinkingrealms.backend.dto.question.AttemptRequest;
import com.thinkingrealms.backend.dto.question.AttemptResultResponse;
import com.thinkingrealms.backend.dto.reward.RewardEventDto;
import com.thinkingrealms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The heart of the platform: evaluates an attempt, updates both mastery
 * profiles, computes XP/level/achievements/region-unlock, and returns an
 * ORDERED list of RewardEvents for the frontend RewardSequencer to play.
 *
 * The backend decides WHAT happened; the frontend decides HOW it looks.
 */
@Service
@RequiredArgsConstructor
public class AttemptService {

    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final GameRegionRepository gameRegionRepository;
    private final AnswerValidationService answerValidationService;
    private final MasteryService masteryService;
    private final XpService xpService;
    private final AchievementService achievementService;

    @Transactional
    public AttemptResultResponse submitAttempt(UUID studentId, UUID questionId, AttemptRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        boolean correct = answerValidationService.isCorrect(request.submittedAnswer(), question.getCorrectAnswer());

        Attempt attempt = new Attempt();
        attempt.setStudentId(studentId);
        attempt.setQuestion(question);
        attempt.setSubmittedAnswer(request.submittedAnswer());
        attempt.setCorrect(correct);
        attempt.setHintsUsed(request.hintsUsed());
        attempt.setTimeTakenSeconds(request.timeTakenSeconds());
        attemptRepository.save(attempt);

        masteryService.updateMathMastery(studentId, question.getMathTopic(), correct, request.hintsUsed());
        masteryService.updateThinkingMastery(studentId, question.getThinkingSkillSlugs(), correct, request.hintsUsed());

        List<RewardEventDto> events = new ArrayList<>();

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new IllegalStateException("Student profile missing for user " + studentId));

        if (correct) {
            int previousXp = profile.getTotalXp();
            int xpGained = question.getXpReward();
            int newXp = previousXp + xpGained;
            profile.setTotalXp(newXp);

            events.add(new RewardEventDto("xpGained", Map.of(
                    "amount", xpGained,
                    "totalXp", newXp
            )));

            List<Achievement> newAchievements = achievementService.evaluate(studentId, true);
            for (Achievement achievement : newAchievements) {
                events.add(new RewardEventDto("achievementUnlocked", Map.of(
                        "code", achievement.getCode(),
                        "name", achievement.getName(),
                        "description", achievement.getDescription() == null ? "" : achievement.getDescription()
                )));
            }

            if (xpService.crossedLevelBoundary(previousXp, newXp)) {
                events.add(new RewardEventDto("levelUp", Map.of(
                        "newLevel", xpService.levelForXp(newXp)
                )));
            }

            gameRegionRepository.findAll().stream()
                    .filter(r -> r.getUnlockXpThreshold() > previousXp && r.getUnlockXpThreshold() <= newXp)
                    .findFirst()
                    .ifPresent(region -> events.add(new RewardEventDto("regionUnlocked", Map.of(
                            "regionId", region.getId().toString(),
                            "regionName", region.getName(),
                            "regionSlug", region.getSlug()
                    ))));

            studentProfileRepository.save(profile);
        }

        return new AttemptResultResponse(
                correct,
                question.getCorrectAnswer(),
                question.getExplanation(),
                correct ? null : question.getCommonMistakeNote(),
                events
        );
    }
}
