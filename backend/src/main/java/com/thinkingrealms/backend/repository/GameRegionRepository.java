package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.GameRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameRegionRepository extends JpaRepository<GameRegion, UUID> {
    List<GameRegion> findByWorldIdOrderByOrderIndexAsc(UUID worldId);
}
