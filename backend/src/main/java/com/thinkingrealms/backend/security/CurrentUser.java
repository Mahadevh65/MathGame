package com.thinkingrealms.backend.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/** Small helper to pull the authenticated student's id out of the SecurityContext. */
public final class CurrentUser {

    private CurrentUser() {}

    public static UUID id() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        throw new IllegalStateException("No authenticated user in context");
    }
}
