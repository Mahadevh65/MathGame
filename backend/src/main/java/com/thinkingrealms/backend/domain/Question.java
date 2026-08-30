package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 2000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Column(nullable = false)
    private int difficulty; // 1 (easiest) - 10 (hardest)

    @ManyToOne(optional = false)
    @JoinColumn(name = "math_topic_id")
    private MathTopic mathTopic;

    /** Thinking skills this question exercises, referenced by slug for simplicity. */
    @ElementCollection
    @CollectionTable(name = "question_thinking_skills", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "thinking_skill_slug")
    private List<String> thinkingSkillSlugs = new ArrayList<>();

    /** Canonical correct answer. Validated via AnswerValidationService, not raw string equality. */
    @Column(nullable = false)
    private String correctAnswer;

    @Column(length = 2000)
    private String explanation;

    @ElementCollection
    @CollectionTable(name = "question_hints", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "hint_order")
    @Column(name = "hint_text", length = 1000)
    private List<String> hints = new ArrayList<>();

    @Column(nullable = false)
    private int xpReward = 10;

    @Column(nullable = false)
    private int expectedTimeSeconds = 60;

    @Column(length = 1000)
    private String commonMistakeNote;
}
