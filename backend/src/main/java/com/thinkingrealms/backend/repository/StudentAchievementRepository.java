package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.StudentAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudentAchievementRepository extends JpaRepository<StudentAchievement, UUID> {
    List<StudentAchievement> findByStudentId(UUID studentId);
    boolean existsByStudentIdAndAchievementId(UUID studentId, UUID achievementId);
}
