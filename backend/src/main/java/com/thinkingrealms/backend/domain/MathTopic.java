package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "math_topics")
@Getter
@Setter
@NoArgsConstructor
public class MathTopic {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug; // e.g. "fractions"

    @Column(nullable = false)
    private String name; // e.g. "Fractions"

    @Column(length = 1000)
    private String description;

    private int orderIndex;
}
