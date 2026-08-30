package com.thinkingrealms.backend.service;

import com.thinkingrealms.backend.domain.StudentProfile;
import com.thinkingrealms.backend.domain.ThinkingSkill;
import com.thinkingrealms.backend.dto.student.MasteryEntryDto;
import com.thinkingrealms.backend.dto.student.ProgressResponse;
import com.thinkingrealms.backend.dto.student.ThinkingProfileResponse;
import com.thinkingrealms.backend.repository.SkillMasteryRepository;
import com.thinkingrealms.backend.repository.StudentProfileRepository;
import com.thinkingrealms.backend.repository.ThinkingSkillMasteryRepository;
import com.thinkingrealms.backend.repository.ThinkingSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final SkillMasteryRepository skillMasteryRepository;
    private final ThinkingSkillMasteryRepository thinkingSkillMasteryRepository;
    private final ThinkingSkillRepository thinkingSkillRepository;
    private final XpService xpService;

    public ProgressResponse getProgress(UUID studentId) {
        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new IllegalStateException("Student profile missing"));

        List<MasteryEntryDto> mastery = skillMasteryRepository.findByStudentId(studentId).stream()
                .map(sm -> new MasteryEntryDto(sm.getMathTopic().getSlug(), sm.getMathTopic().getName(), sm.getMasteryPercent()))
                .toList();

        return new ProgressResponse(
                profile.getTotalXp(),
                xpService.levelForXp(profile.getTotalXp()),
                xpService.xpIntoCurrentLevel(profile.getTotalXp()),
                xpService.xpNeededForNextLevel(),
                profile.getCurrentRegionId(),
                profile.getCurrentMissionId(),
                mastery
        );
    }

    public ThinkingProfileResponse getThinkingProfile(UUID studentId) {
        List<MasteryEntryDto> entries = thinkingSkillMasteryRepository.findByStudentId(studentId).stream()
                .map(tsm -> {
                    ThinkingSkill skill = thinkingSkillRepository.findBySlug(tsm.getThinkingSkillSlug()).orElse(null);
                    String name = skill != null ? skill.getName() : tsm.getThinkingSkillSlug();
                    return new MasteryEntryDto(tsm.getThinkingSkillSlug(), name, tsm.getMasteryPercent());
                })
                .toList();

        return new ThinkingProfileResponse(entries);
    }
}
