package com.thinkingrealms.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XpServiceTest {

    private final XpService xpService = new XpService();

    @Test
    void calculatesLevelFromTotalXp() {
        assertEquals(1, xpService.levelForXp(0));
        assertEquals(1, xpService.levelForXp(99));
        assertEquals(2, xpService.levelForXp(100));
        assertEquals(3, xpService.levelForXp(250));
    }

    @Test
    void detectsLevelBoundaryCrossing() {
        assertTrue(xpService.crossedLevelBoundary(95, 105));
        assertFalse(xpService.crossedLevelBoundary(20, 90));
        assertFalse(xpService.crossedLevelBoundary(100, 100));
    }
}
