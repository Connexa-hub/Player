package com.brouken.player.core.gestures;

/**
 * Pure brightness-level math extracted from {@code BrightnessControl}. No Android dependency —
 * this module is plain JVM code, which is the point: the actual decision logic behind a swipe
 * gesture doesn't need a {@code WindowManager} to be correct, only to be applied.
 */
public final class BrightnessCurve {

    private BrightnessCurve() {}

    /** Sentinel meaning "follow the system's automatic brightness". */
    public static final int LEVEL_AUTO = -1;
    public static final int LEVEL_MIN = 0;
    public static final int LEVEL_MAX = 30;

    /**
     * Given the current discrete level (0..30, or {@link #LEVEL_AUTO}), returns the next level
     * after a swipe-up ({@code increase = true}) or swipe-down gesture. Mirrors
     * {@code BrightnessControl#changeBrightness} exactly: stepping below 0 drops to
     * {@link #LEVEL_AUTO} only if the device supports it ({@code canSetAuto}); otherwise the
     * level simply doesn't move past the boundary.
     */
    public static int nextLevel(int currentLevel, boolean increase, boolean canSetAuto) {
        int candidate = increase ? currentLevel + 1 : currentLevel - 1;
        if (canSetAuto && candidate < LEVEL_MIN) {
            return LEVEL_AUTO;
        } else if (candidate >= LEVEL_MIN && candidate <= LEVEL_MAX) {
            return candidate;
        }
        return currentLevel;
    }

    /**
     * Converts a discrete 0..30 level into a screen-brightness fraction using the same
     * perceptual (roughly gamma-2) curve as the legacy code, so low levels stay usable instead
     * of clipping to near-black.
     */
    public static float levelToBrightness(int level) {
        final double d = 0.064 + 0.936 / (double) LEVEL_MAX * (double) level;
        return (float) (d * d);
    }
}
