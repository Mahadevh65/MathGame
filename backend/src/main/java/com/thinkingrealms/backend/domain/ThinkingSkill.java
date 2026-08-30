package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "thinking_skills")
@Getter
@Setter
@NoArgsConstructor
public class ThinkingSkill {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug; // e.g. "pattern-recognition"

    @Column(nullable = false)
    private String name; // e.g. "Pattern Recognition"

    @Column(length = 1000)
    private String description;
}
