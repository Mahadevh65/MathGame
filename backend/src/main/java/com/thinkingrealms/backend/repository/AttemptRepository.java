package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttemptRepository extends JpaRepository<Attempt, UUID> {
    List<Attempt> findByStudentIdOrderByCreatedAtDesc(UUID studentId);
    long countByStudentIdAndCorrectTrue(UUID studentId);
}
