package com.brouken.player.core.playback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.view.accessibility.CaptioningManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.extractor.DefaultExtractorsFactory;
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory;
import androidx.media3.extractor.ts.TsExtractor;
import androidx.media3.session.MediaSession;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Builds a configured {@link ExoPlayer} + {@link DefaultTrackSelector} + (optional)
 * {@link MediaSession}, extracted verbatim (behaviorally) from
 * {@code PlayerActivity#initializePlayer()} in the Phase 0 modularization pass.
 *
 * Deliberately does NOT touch UI (PlayerView, title, timebar, gestures, subtitle overlays,
 * loudness enhancer, "next file" lookahead) — those stay in {@code PlayerActivity} for now and
 * move out in later phases. This class owns exactly one concern: constructing a correctly
 * configured playback engine instance.
 */
public final class PlaybackEngine {

    private PlaybackEngine() {}

    public static final class Result {
        @NonNull public final ExoPlayer player;
        @NonNull public final DefaultTrackSelector trackSelector;
        @Nullable public final MediaSession mediaSession;

        Result(@NonNull ExoPlayer player, @NonNull DefaultTrackSelector trackSelector,
               @Nullable MediaSession mediaSession) {
            this.player = player;
            this.trackSelector = trackSelector;
            this.mediaSession = mediaSession;
        }
    }

    /**
     * @param context        Activity/Application context.
     * @param config         Resolved playback settings (see {@link PlaybackConfig}).
     * @param captioningManager the platform captioning service; passed in rather than resolved
     *                          internally so this method has no hidden Context.getSystemService
     *                          dependency and stays trivially testable.
     */
    @SuppressLint("WrongConstant")
    public static Result createPlayer(@NonNull Context context,
                                       @NonNull PlaybackConfig config,
                                       @NonNull CaptioningManager captioningManager) {
        DefaultTrackSelector trackSelector = buildTrackSelector(context, config, captioningManager);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
                // https://github.com/google/ExoPlayer/issues/8571
                .setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS)
                .setTsExtractorTimestampSearchBytes(1500 * TsExtractor.TS_PACKET_SIZE);

        RenderersFactory renderersFactory = new DefaultRenderersFactory(context)
                .setExtensionRendererMode(config.decoderPriority())
                .setMapDV7ToHevc(config.mapDolbyVision7ToHevc());

        ExoPlayer.Builder playerBuilder = new ExoPlayer.Builder(context, renderersFactory)
                .setTrackSelector(trackSelector)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(context, extractorsFactory));

        Map<String, String> basicAuthHeaders = buildBasicAuthHeaders(config.mediaUri());
        if (basicAuthHeaders != null) {
            DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
            httpDataSourceFactory.setDefaultRequestProperties(basicAuthHeaders);
            playerBuilder.setMediaSourceFactory(new DefaultMediaSourceFactory(httpDataSourceFactory, extractorsFactory));
        }

        ExoPlayer player = playerBuilder.build();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build();
        player.setAudioAttributes(audioAttributes, true);

        if (config.skipSilenceEnabled()) {
            player.setSkipSilenceEnabled(true);
        }

        MediaSession mediaSession = null;
        if (player.canAdvertiseSession()) {
            try {
                mediaSession = new MediaSession.Builder(context, player).build();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        return new Result(player, trackSelector, mediaSession);
    }

    static DefaultTrackSelector buildTrackSelector(@NonNull Context context,
                                                     @NonNull PlaybackConfig config,
                                                     @NonNull CaptioningManager captioningManager) {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
        trackSelector.setParameters(trackSelector.buildUponParameters()
                .setAllowInvalidateSelectionsOnRendererCapabilitiesChange(true));

        if (config.tunnelingEnabled()) {
            trackSelector.setParameters(trackSelector.buildUponParameters()
                    .setTunnelingEnabled(true));
        }

        String languageMode = config.audioLanguageMode();
        if (PlaybackConfig.TRACK_DEFAULT.equals(languageMode)) {
            // media file defaults, nothing to set
        } else if (PlaybackConfig.TRACK_DEVICE.equals(languageMode)) {
            trackSelector.setParameters(trackSelector.buildUponParameters()
                    .setPreferredAudioLanguages(config.deviceLanguages()));
        } else {
            trackSelector.setParameters(trackSelector.buildUponParameters()
                    .setPreferredAudioLanguages(languageMode));
        }

        if (!captioningManager.isEnabled()) {
            trackSelector.setParameters(trackSelector.buildUponParameters()
                    .setIgnoredTextSelectionFlags(C.SELECTION_FLAG_DEFAULT));
        }
        Locale locale = captioningManager.getLocale();
        if (locale != null) {
            trackSelector.setParameters(trackSelector.buildUponParameters()
                    .setPreferredTextLanguage(locale.getISO3Language()));
        }

        return trackSelector;
    }

    /**
     * Mirrors the legacy inline logic: if the media URI carries HTTP Basic Auth user-info
     * ({@code http://user:pass@host/...}), build the header map for it. Returns {@code null}
     * when there's nothing to add (no headers factory override needed).
     */
    @Nullable
    static Map<String, String> buildBasicAuthHeaders(@Nullable Uri mediaUri) {
        if (mediaUri == null || mediaUri.getScheme() == null) {
            return null;
        }
        if (!mediaUri.getScheme().toLowerCase(Locale.ROOT).startsWith("http")) {
            return null;
        }
        String userInfo = mediaUri.getUserInfo();
        if (userInfo == null || userInfo.length() == 0 || !userInfo.contains(":")) {
            return null;
        }
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.encodeToString(userInfo.getBytes(), Base64.NO_WRAP));
        return headers;
    }
}
