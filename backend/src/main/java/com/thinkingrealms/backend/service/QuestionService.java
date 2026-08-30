package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.Question;
import com.thinkingrealms.backend.dto.question.HintResponse;
import com.thinkingrealms.backend.dto.question.QuestionResponse;
import com.thinkingrealms.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class-level @Transactional(readOnly = true) keeps the Hibernate session
 * open for the full duration of every method here, including the mapping
 * logic in toResponse() that touches lazy @ElementCollection fields
 * (thinkingSkillSlugs, hints). Without it, open-in-view: false (correct
 * production setting - see application.yml) causes a
 * LazyInitializationException the moment any caller or the JSON serializer
 * tries to read those collections after the repository call returns.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionResponse getQuestion(UUID id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + id));
        return toResponse(q);
    }

    public HintResponse getHint(UUID questionId, int hintIndex) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        if (hintIndex < 0 || hintIndex >= q.getHints().size()) {
            throw new IllegalArgumentException("No hint at index " + hintIndex);
        }

        int remaining = q.getHints().size() - (hintIndex + 1);
        return new HintResponse(hintIndex, q.getHints().get(hintIndex), remaining);
    }

    public QuestionResponse toResponse(Question q) {
        // Force lazy collections to materialize into plain ArrayLists while the
        // session is still open, so callers (and Jackson, if this DTO is
        // returned directly to a controller from outside this transaction)
        // never touch a Hibernate proxy after the session has closed.
        List<String> thinkingSkills = new ArrayList<>(q.getThinkingSkillSlugs());

        return new QuestionResponse(
                q.getId(),
                q.getQuestionText(),
                q.getQuestionType().name(),
                q.getDifficulty(),
                q.getMathTopic().getSlug(),
                thinkingSkills,
                q.getExpectedTimeSeconds(),
                q.getHints().size()
        );
    }
}
