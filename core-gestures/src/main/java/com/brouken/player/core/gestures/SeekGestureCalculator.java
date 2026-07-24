package com.brouken.player.core.gestures;

/**
 * Pure swipe-to-seek math extracted from {@code CustomPlayerView#onScroll}'s horizontal branch.
 * All view/player side effects (icons, controller visibility, {@code ExoPlayer.seekTo}) stay in
 * the view; this class only answers "given the current seek state and swipe distance, what
 * should the new position be, and is the move actually allowed".
 *
 * <p>Note on numeric fidelity: the legacy code computed {@code SEEK_STEP * distanceDiff} in
 * float precision and let it truncate into a {@code long} via compound assignment. This class
 * reproduces that same float-then-truncate order so seek amounts match to the millisecond in
 * all practical swipe ranges; it is not claimed to be bit-identical for pathological inputs,
 * which don't occur in a touch-gesture context anyway.
 */
public final class SeekGestureCalculator {

    private SeekGestureCalculator() {}

    /**
     * Converts a horizontal swipe distance (in dp) into a seek-speed multiplier, clamped to
     * [0.5, 10] exactly as the legacy code did — small movements seek slowly, fast swipes seek
     * fast, with a ceiling so a single frame of a fling doesn't jump minutes at once.
     */
    public static float distanceDiff(float dpDistanceX) {
        return Math.max(0.5f, Math.min(Math.abs(dpDistanceX) / 4f, 10f));
    }

    /** Result of attempting a seek step: whether it was allowed, and the resulting state if so. */
    public static final class Seek {
        public final long seekChange;
        public final long position;
        public final boolean applied;

        private Seek(long seekChange, long position, boolean applied) {
            this.seekChange = seekChange;
            this.position = position;
            this.applied = applied;
        }

        private static Seek notApplied(long currentSeekChange) {
            return new Seek(currentSeekChange, 0L, false);
        }
    }

    /**
     * Swipe left (rewind). Refuses to seek past position 0.
     *
     * @param seekStart   playback position (ms) when the current gesture began
     * @param seekChange  accumulated seek offset (ms) applied so far this gesture
     * @param distanceDiff  from {@link #distanceDiff(float)}
     * @param seekStepMs  base step size in ms (1000 in the legacy code)
     */
    public static Seek backward(long seekStart, long seekChange, float distanceDiff, long seekStepMs) {
        float delta = seekStepMs * distanceDiff;
        if (seekStart + seekChange - delta >= 0) {
            long newSeekChange = (long) (seekChange - delta);
            return new Seek(newSeekChange, seekStart + newSeekChange, true);
        }
        return Seek.notApplied(seekChange);
    }

    /**
     * Swipe right (fast-forward). Refuses to seek past the known duration, unless the duration
     * is unknown (live/unset), in which case it's always allowed.
     *
     * @param seekMax   playback duration (ms), or {@code timeUnset} if unknown
     * @param timeUnset the caller's sentinel for "unknown duration" (media3's {@code C.TIME_UNSET})
     */
    public static Seek forward(long seekStart, long seekChange, float distanceDiff, long seekStepMs,
                                long seekMax, long timeUnset) {
        float delta = seekStepMs * distanceDiff;
        if (seekMax == timeUnset) {
            long newSeekChange = (long) (seekChange + delta);
            return new Seek(newSeekChange, seekStart + newSeekChange, true);
        } else if (seekStart + seekChange + seekStepMs < seekMax) {
            long newSeekChange = (long) (seekChange + delta);
            return new Seek(newSeekChange, seekStart + newSeekChange, true);
        }
        return Seek.notApplied(seekChange);
    }
}
