package com.thinkingrealms.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "game_regions")
@Getter
@Setter
@NoArgsConstructor
public class GameRegion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_world_id")
    private GameWorld world;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private int orderIndex;

    /** Total XP the student must have accumulated for this region to unlock. */
    @Column(nullable = false)
    private int unlockXpThreshold;

    /** Theme hint the frontend uses to pick colors/art for this region. */
    private String themeSlug;
}
