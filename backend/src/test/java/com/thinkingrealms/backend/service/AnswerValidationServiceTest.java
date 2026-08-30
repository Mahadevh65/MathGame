package com.thinkingrealms.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnswerValidationServiceTest {

    private final AnswerValidationService service = new AnswerValidationService();

    @Test
    void treatsEquivalentNumericFormsAsCorrect() {
        assertTrue(service.isCorrect("0.5", "1/2"));
        assertTrue(service.isCorrect("50%", "1/2"));
        assertTrue(service.isCorrect("0.5", "50%"));
        assertTrue(service.isCorrect("3/4", "0.75"));
    }

    @Test
    void rejectsIncorrectNumericAnswers() {
        assertFalse(service.isCorrect("0.4", "1/2"));
        assertFalse(service.isCorrect("40%", "1/2"));
    }

    @Test
    void fallsBackToNormalizedStringComparisonForNonNumericAnswers() {
        assertTrue(service.isCorrect(" Incorrect ", "incorrect"));
        assertTrue(service.isCorrect("Distribute", "distribute"));
        assertFalse(service.isCorrect("correct", "incorrect"));
    }
}
