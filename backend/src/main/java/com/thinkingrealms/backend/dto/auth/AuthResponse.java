package com.thinkingrealms.backend.dto.auth;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        String displayName,
        String role
) {}
