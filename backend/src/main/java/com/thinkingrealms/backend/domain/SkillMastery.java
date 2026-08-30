package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "skill_mastery", uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "math_topic_id"}))
@Getter
@Setter
@NoArgsConstructor
public class SkillMastery {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "math_topic_id")
    private MathTopic mathTopic;

    @Column(nullable = false)
    private double masteryPercent = 0.0;

    @Column(nullable = false)
    private int attemptsCount = 0;
}
