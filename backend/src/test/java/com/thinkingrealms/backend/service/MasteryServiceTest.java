package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.MathTopic;
import com.thinkingrealms.backend.domain.SkillMastery;
import com.thinkingrealms.backend.repository.SkillMasteryRepository;
import com.thinkingrealms.backend.repository.ThinkingSkillMasteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MasteryServiceTest {

    private SkillMasteryRepository skillMasteryRepository;
    private ThinkingSkillMasteryRepository thinkingSkillMasteryRepository;
    private MasteryService masteryService;

    @BeforeEach
    void setUp() {
        skillMasteryRepository = mock(SkillMasteryRepository.class);
        thinkingSkillMasteryRepository = mock(ThinkingSkillMasteryRepository.class);
        masteryService = new MasteryService(skillMasteryRepository, thinkingSkillMasteryRepository);

        when(skillMasteryRepository.findByStudentIdAndMathTopicId(any(), any())).thenReturn(Optional.empty());
        when(skillMasteryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void correctAnswerIncreasesMasteryTowardTarget() {
        MathTopic topic = new MathTopic();
        topic.setId(UUID.randomUUID());
        topic.setSlug("fractions");
        topic.setName("Fractions");

        double result = masteryService.updateMathMastery(UUID.randomUUID(), topic, true, 0);

        assertTrue(result > 0, "Mastery should move up from 0 after a correct, hint-free answer");
    }

    @Test
    void incorrectAnswerDoesNotIncreaseMasteryAboveZeroBaseline() {
        MathTopic topic = new MathTopic();
        topic.setId(UUID.randomUUID());
        topic.setSlug("fractions");
        topic.setName("Fractions");

        SkillMastery existing = new SkillMastery();
        existing.setMasteryPercent(40.0);
        when(skillMasteryRepository.findByStudentIdAndMathTopicId(any(), any())).thenReturn(Optional.of(existing));

        double result = masteryService.updateMathMastery(UUID.randomUUID(), topic, false, 0);

        assertTrue(result < 40.0, "An incorrect answer should pull mastery down toward 0");
    }
}
