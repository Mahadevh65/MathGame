package com.thinkingrealms.backend.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deterministic math answer validation. Never relies on the AI tutor.
 * Understands that 0.5, 1/2 and 50% can represent the same value where
 * that equivalence is meaningful (numeric answers). Falls back to
 * normalized string comparison for non-numeric answers (e.g. "x=4",
 * free-text explanations are graded separately, not here).
 */
@Service
public class AnswerValidationService {

    private static final Pattern FRACTION = Pattern.compile("^(-?\\d+)\\s*/\\s*(\\d+)$");
    private static final Pattern PERCENT = Pattern.compile("^(-?\\d+(\\.\\d+)?)\\s*%$");

    public boolean isCorrect(String submitted, String expected) {
        if (submitted == null || expected == null) return false;

        String s = submitted.trim();
        String e = expected.trim();

        // Try numeric-equivalence comparison first.
        BigDecimal sVal = toNumeric(s);
        BigDecimal eVal = toNumeric(e);
        if (sVal != null && eVal != null) {
            return sVal.compareTo(eVal) == 0;
        }

        // Fall back to normalized string comparison (case-insensitive, whitespace-trimmed).
        return normalize(s).equalsIgnoreCase(normalize(e));
    }

    private String normalize(String value) {
        return value.replaceAll("\\s+", "").toLowerCase();
    }

    /** Returns a BigDecimal if the value is numeric, a fraction, or a percentage; otherwise null. */
    private BigDecimal toNumeric(String value) {
        String v = value.trim();

        Matcher fracMatcher = FRACTION.matcher(v);
        if (fracMatcher.matches()) {
            BigDecimal numerator = new BigDecimal(fracMatcher.group(1));
            BigDecimal denominator = new BigDecimal(fracMatcher.group(2));
            if (denominator.compareTo(BigDecimal.ZERO) == 0) return null;
            return numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        }

        Matcher pctMatcher = PERCENT.matcher(v);
        if (pctMatcher.matches()) {
            BigDecimal pct = new BigDecimal(pctMatcher.group(1));
            return pct.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        }

        try {
            return new BigDecimal(v).stripTrailingZeros();
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
