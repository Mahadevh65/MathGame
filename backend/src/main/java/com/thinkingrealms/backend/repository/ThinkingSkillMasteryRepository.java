package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.ThinkingSkillMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThinkingSkillMasteryRepository extends JpaRepository<ThinkingSkillMastery, UUID> {
    Optional<ThinkingSkillMastery> findByStudentIdAndThinkingSkillSlug(UUID studentId, String slug);
    List<ThinkingSkillMastery> findByStudentId(UUID studentId);
}
