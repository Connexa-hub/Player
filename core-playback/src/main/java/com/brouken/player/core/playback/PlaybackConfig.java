package com.brouken.player.core.playback;

import android.net.Uri;

import androidx.annotation.Nullable;

/**
 * Everything {@link PlaybackEngine} needs to know to build a player, decoupled from the app's
 * concrete settings storage (SharedPreferences-backed {@code Prefs} in {@code app}).
 *
 * This is the seam that makes {@link PlaybackEngine} unit-testable and reusable outside of
 * {@code PlayerActivity} (e.g. from a future service, a desktop shell, or tests) without dragging
 * in the rest of the app.
 */
public interface PlaybackConfig {

    /** Audio-language selection mode constants, mirrored from {@code Prefs}. */
    String TRACK_DEFAULT = "default";
    String TRACK_DEVICE = "device";

    boolean tunnelingEnabled();

    /** One of {@link #TRACK_DEFAULT}, {@link #TRACK_DEVICE}, or a literal language tag/list. */
    String audioLanguageMode();

    /** Used only when {@link #audioLanguageMode()} is {@link #TRACK_DEVICE}. */
    String[] deviceLanguages();

    /** {@code DefaultRenderersFactory.EXTENSION_RENDERER_MODE_*} */
    int decoderPriority();

    boolean mapDolbyVision7ToHevc();

    boolean skipSilenceEnabled();

    @Nullable
    Uri mediaUri();

    @Nullable
    String mediaMimeType();

    long resumePositionMs();
}
