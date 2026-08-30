package com.thinkingrealms.backend.controller;

import com.thinkingrealms.backend.dto.student.ProgressResponse;
import com.thinkingrealms.backend.dto.student.ThinkingProfileResponse;
import com.thinkingrealms.backend.security.CurrentUser;
import com.thinkingrealms.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students/me")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/progress")
    public ResponseEntity<ProgressResponse> getProgress() {
        return ResponseEntity.ok(studentService.getProgress(CurrentUser.id()));
    }

    @GetMapping("/thinking-profile")
    public ResponseEntity<ThinkingProfileResponse> getThinkingProfile() {
        return ResponseEntity.ok(studentService.getThinkingProfile(CurrentUser.id()));
    }
}
