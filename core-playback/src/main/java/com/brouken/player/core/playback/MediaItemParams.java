package com.brouken.player.core.playback;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;

import java.util.Collections;
import java.util.List;

/**
 * What {@link MediaItemFactory} needs to build a {@link MediaItem}. Title and subtitle
 * *resolution* (API-provided vs. filename-derived title; API-provided vs. on-disk subtitle file)
 * stays app-side, since that logic depends on {@code Utils}/{@code SubtitleUtils}/{@code Context}.
 * This class only carries the already-resolved result.
 */
public final class MediaItemParams {

    public final Uri uri;
    @Nullable public final String mimeType;
    @Nullable public final String title;
    public final List<MediaItem.SubtitleConfiguration> subtitleConfigurations;

    public MediaItemParams(Uri uri, @Nullable String mimeType, @Nullable String title,
                            @Nullable List<MediaItem.SubtitleConfiguration> subtitleConfigurations) {
        this.uri = uri;
        this.mimeType = mimeType;
        this.title = title;
        this.subtitleConfigurations = subtitleConfigurations == null
                ? Collections.emptyList()
                : subtitleConfigurations;
    }
}
