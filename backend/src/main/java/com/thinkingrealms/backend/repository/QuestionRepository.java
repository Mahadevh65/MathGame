package com.thinkingrealms.backend.repository;

import com.thinkingrealms.backend.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
