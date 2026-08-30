package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.GameWorld;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameWorldRepository extends JpaRepository<GameWorld, UUID> {
    Optional<GameWorld> findBySlug(String slug);
}
