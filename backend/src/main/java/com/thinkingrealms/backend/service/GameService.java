package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.GameRegion;
import com.thinkingrealms.backend.domain.GameWorld;
import com.thinkingrealms.backend.domain.Mission;
import com.thinkingrealms.backend.domain.StudentProfile;
import com.thinkingrealms.backend.dto.game.MissionResponse;
import com.thinkingrealms.backend.dto.game.RegionResponse;
import com.thinkingrealms.backend.dto.game.WorldResponse;
import com.thinkingrealms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class-level @Transactional(readOnly = true) keeps the Hibernate session
 * open for the duration of every method here. This matters specifically
 * for getMissionsForRegion() and getMission(), which read Mission's lazy
 * @ElementCollection questionIds - without an open session, that throws
 * LazyInitializationException the moment .size()/.stream() touches it
 * (see application.yml: open-in-view is false, which is the correct
 * production setting but requires explicit transaction boundaries here).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameWorldRepository gameWorldRepository;
    private final GameRegionRepository gameRegionRepository;
    private final MissionRepository missionRepository;
    private final QuestionRepository questionRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final QuestionService questionService;

    public List<WorldResponse> getWorldsForStudent(UUID studentId) {
        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new IllegalStateException("Student profile missing"));

        return gameWorldRepository.findAll().stream()
                .map(world -> toWorldResponse(world, profile.getTotalXp()))
                .toList();
    }

    private WorldResponse toWorldResponse(GameWorld world, int studentXp) {
        List<RegionResponse> regions = gameRegionRepository.findByWorldIdOrderByOrderIndexAsc(world.getId())
                .stream()
                .map(region -> toRegionResponse(region, studentXp))
                .toList();

        return new WorldResponse(world.getId(), world.getSlug(), world.getName(), world.getDescription(), regions);
    }

    private RegionResponse toRegionResponse(GameRegion region, int studentXp) {
        boolean unlocked = studentXp >= region.getUnlockXpThreshold();
        return new RegionResponse(
                region.getId(), region.getSlug(), region.getName(), region.getDescription(),
                region.getOrderIndex(), region.getUnlockXpThreshold(), region.getThemeSlug(), unlocked
        );
    }

    public List<com.thinkingrealms.backend.dto.game.MissionSummaryResponse> getMissionsForRegion(UUID regionId) {
        return missionRepository.findByRegionIdOrderByOrderIndexAsc(regionId).stream()
                .map(m -> new com.thinkingrealms.backend.dto.game.MissionSummaryResponse(
                        m.getId(), m.getName(), m.getDescription(), m.isBoss(), m.getQuestionIds().size()
                ))
                .toList();
    }

    public MissionResponse getMission(UUID missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Mission not found: " + missionId));

        var questions = mission.getQuestionIds().stream()
                .map(qid -> questionRepository.findById(qid).orElseThrow())
                .map(questionService::toResponse)
                .collect(Collectors.toList());

        return new MissionResponse(mission.getId(), mission.getName(), mission.getDescription(), mission.isBoss(), questions);
    }
}
