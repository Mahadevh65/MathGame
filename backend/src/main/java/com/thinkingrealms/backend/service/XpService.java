package com.thinkingrealms.backend.service;

import org.springframework.stereotype.Service;

/**
 * Simple, transparent leveling curve: 100 XP per level.
 * Kept centralized so frontend and backend never disagree on level math -
 * the frontend only ever reads the values this service already computed.
 */
@Service
public class XpService {

    public static final int XP_PER_LEVEL = 100;

    public int levelForXp(int totalXp) {
        return 1 + (totalXp / XP_PER_LEVEL);
    }

    public int xpIntoCurrentLevel(int totalXp) {
        return totalXp % XP_PER_LEVEL;
    }

    public int xpNeededForNextLevel() {
        return XP_PER_LEVEL;
    }

    public boolean crossedLevelBoundary(int previousXp, int newXp) {
        return levelForXp(newXp) > levelForXp(previousXp);
    }
}
