package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.ThinkingSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ThinkingSkillRepository extends JpaRepository<ThinkingSkill, UUID> {
    Optional<ThinkingSkill> findBySlug(String slug);
}
