package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "thinking_skill_mastery", uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "thinkingSkillSlug"}))
@Getter
@Setter
@NoArgsConstructor
public class ThinkingSkillMastery {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private String thinkingSkillSlug;

    @Column(nullable = false)
    private double masteryPercent = 0.0;

    @Column(nullable = false)
    private int attemptsCount = 0;
}
