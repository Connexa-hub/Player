package com.brouken.player;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.brouken.player.core.playback.PlaybackConfig;

/**
 * Adapts the app's existing {@link Prefs} (SharedPreferences-backed) to the decoupled
 * {@link PlaybackConfig} contract consumed by {@code core-playback}. Keeps {@code Prefs} itself
 * untouched so nothing else in the app is at risk from this refactor.
 */
final class PrefsPlaybackConfig implements PlaybackConfig {

    private final Prefs prefs;
    private final String[] deviceLanguages;

    PrefsPlaybackConfig(Prefs prefs, String[] deviceLanguages) {
        this.prefs = prefs;
        this.deviceLanguages = deviceLanguages;
    }

    @Override
    public boolean tunnelingEnabled() {
        return prefs.tunneling;
    }

    @Override
    public String audioLanguageMode() {
        return prefs.languageAudio;
    }

    @Override
    public String[] deviceLanguages() {
        return deviceLanguages;
    }

    @Override
    public int decoderPriority() {
        return prefs.decoderPriority;
    }

    @Override
    public boolean mapDolbyVision7ToHevc() {
        return prefs.mapDV7ToHevc;
    }

    @Override
    public boolean skipSilenceEnabled() {
        return prefs.skipSilence;
    }

    @Nullable
    @Override
    public Uri mediaUri() {
        return prefs.mediaUri;
    }

    @Nullable
    @Override
    public String mediaMimeType() {
        return prefs.mediaType;
    }

    @Override
    public long resumePositionMs() {
        return prefs.getPosition();
    }
}
