package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "missions")
@Getter
@Setter
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_region_id")
    private GameRegion region;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private int orderIndex;

    @Column(nullable = false)
    private boolean isBoss = false;

    @ElementCollection
    @CollectionTable(name = "mission_questions", joinColumns = @JoinColumn(name = "mission_id"))
    @OrderColumn(name = "question_order")
    @Column(name = "question_id")
    private List<UUID> questionIds = new ArrayList<>();
}
