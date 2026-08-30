package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.SkillMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillMasteryRepository extends JpaRepository<SkillMastery, UUID> {
    Optional<SkillMastery> findByStudentIdAndMathTopicId(UUID studentId, UUID mathTopicId);
    List<SkillMastery> findByStudentId(UUID studentId);
}
