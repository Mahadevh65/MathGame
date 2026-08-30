package com.thinkingrealms.backend.controller;

import com.thinkingrealms.backend.dto.game.MissionResponse;
import com.thinkingrealms.backend.dto.game.WorldResponse;
import com.thinkingrealms.backend.security.CurrentUser;
import com.thinkingrealms.backend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/worlds")
    public ResponseEntity<List<WorldResponse>> getWorlds() {
        return ResponseEntity.ok(gameService.getWorldsForStudent(CurrentUser.id()));
    }

    @GetMapping("/missions/{id}")
    public ResponseEntity<MissionResponse> getMission(@PathVariable UUID id) {
        return ResponseEntity.ok(gameService.getMission(id));
    }

    @GetMapping("/regions/{id}/missions")
    public ResponseEntity<List<com.thinkingrealms.backend.dto.game.MissionSummaryResponse>> getMissionsForRegion(@PathVariable UUID id) {
        return ResponseEntity.ok(gameService.getMissionsForRegion(id));
    }
}
