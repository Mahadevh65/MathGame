package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "student_achievements", uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "achievement_id"}))
@Getter
@Setter
@NoArgsConstructor
public class StudentAchievement {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(nullable = false, updatable = false)
    private Instant earnedAt = Instant.now();
}
