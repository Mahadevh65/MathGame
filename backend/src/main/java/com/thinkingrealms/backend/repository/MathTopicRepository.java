package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.MathTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MathTopicRepository extends JpaRepository<MathTopic, UUID> {
    Optional<MathTopic> findBySlug(String slug);
    List<MathTopic> findAllByOrderByOrderIndexAsc();
}
