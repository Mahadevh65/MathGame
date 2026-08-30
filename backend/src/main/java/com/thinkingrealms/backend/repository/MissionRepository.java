package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MissionRepository extends JpaRepository<Mission, UUID> {
    List<Mission> findByRegionIdOrderByOrderIndexAsc(UUID regionId);
}
