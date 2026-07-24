package com.brouken.player.core.library;

/**
 * Decides whether a history entry should show up as "Continue Watching" and, if so, what
 * position to resume from. Pure/static so it's trivially unit-testable and there's exactly one
 * place this policy lives (not duplicated between the DAO query, a ViewModel, and the resume
 * prompt in {@code PlayerActivity}).
 */
public final class ResumePolicy {

    private ResumePolicy() {}

    /** Ignore the first few seconds — nobody wants to "resume" from :03. */
    public static final long MIN_POSITION_MS = 5_000L;

    /** Within this many ms of the end, treat it as finished rather than resumable. */
    public static final long END_THRESHOLD_MS = 15_000L;

    public static boolean isResumable(long positionMs, long durationMs) {
        if (positionMs < MIN_POSITION_MS) {
            return false;
        }
        if (durationMs <= 0) {
            // Unknown duration (e.g. a still-indexing network stream) — allow resume; better to
            // offer a resume point than to silently drop it.
            return true;
        }
        return positionMs < durationMs - END_THRESHOLD_MS;
    }

    /** 0f..1f watched fraction, for a progress indicator on a "Continue Watching" card. */
    public static float watchedFraction(long positionMs, long durationMs) {
        if (durationMs <= 0) {
            return 0f;
        }
        float fraction = (float) positionMs / (float) durationMs;
        return Math.max(0f, Math.min(fraction, 1f));
    }
}
